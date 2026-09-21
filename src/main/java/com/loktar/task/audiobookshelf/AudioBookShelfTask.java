package com.loktar.task.audiobookshelf;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.audiobookshelf.AbsListeningStats;
import com.loktar.dto.audiobookshelf.AbsPlaybackSession;
import com.loktar.dto.audiobookshelf.AbsUser;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.AudioBookShelfUtil;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.RedisUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AudioBookShelfTask {

    // 当日累计收听每满30分钟为一个通知刻度
    private final static int NOTICE_TIER_MINUTES = 30;

    // Redis刻度记录保留2天，避免历史数据堆积
    private final static long TIER_RECORD_EXPIRE = 2 * 24 * 60 * 60;

    private final LokTarConfig lokTarConfig;

    private final RedisUtil redisUtil;

    private final QywxApi qywxApi;

    private final AudioBookShelfUtil audioBookShelfUtil;

    public AudioBookShelfTask(LokTarConfig lokTarConfig, RedisUtil redisUtil, QywxApi qywxApi, AudioBookShelfUtil audioBookShelfUtil) {
        this.lokTarConfig = lokTarConfig;
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
        this.audioBookShelfUtil = audioBookShelfUtil;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void listenMonitor() {
        List<String> users = lokTarConfig.getAudioBookShelf().getUsers();
        if (ObjectUtils.isEmpty(users)) {
            return;
        }
        log.info("{}", "AudioBookShelf收听监控定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        Map<String, String> userIdMap;
        try {
            userIdMap = audioBookShelfUtil.getUserIdMap();
        } catch (Exception e) {
            // 查询失败仅warn并跳过本轮，下轮调度补偿，避免异常上抛触发调度器ERROR日志
            log.warn("AudioBookShelf用户列表查询失败，跳过本轮：{}", e.getMessage());
            return;
        }
        String today = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATE_COMPACT);
        for (String username : users) {
            try {
                monitorUser(username, userIdMap.get(username), today);
            } catch (Exception e) {
                // 单个用户失败不影响本轮其他用户，仅warn不打error堆栈
                log.warn("AudioBookShelf用户[{}]收听监控失败，跳过该用户：{}", username, e.getMessage());
            }
        }
        log.info("{}", "AudioBookShelf收听监控定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
    }

    /**
     * 每天0点将监控用户状态重置为可用
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetUserActive() {
        List<String> monitorUsernames = lokTarConfig.getAudioBookShelf().getUsers();
        if (ObjectUtils.isEmpty(monitorUsernames)) {
            return;
        }
        log.info("{}", "AudioBookShelf监控用户状态重置开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        List<AbsUser> absUsers;
        try {
            absUsers = audioBookShelfUtil.getUsers();
        } catch (Exception e) {
            log.warn("AudioBookShelf用户列表查询失败，跳过本轮重置：{}", e.getMessage());
            return;
        }
        for (AbsUser absUser : absUsers) {
            if (!monitorUsernames.contains(absUser.getUsername()) || Boolean.TRUE.equals(absUser.getIsActive())) {
                continue;
            }
            try {
                audioBookShelfUtil.updateUserActive(absUser.getId(), true);
                log.info("AudioBookShelf用户[{}]已重置为可用状态", absUser.getUsername());
            } catch (Exception e) {
                // 单个用户失败不影响其他用户，仅warn不打error堆栈
                log.warn("AudioBookShelf用户[{}]重置为可用状态失败，跳过该用户：{}", absUser.getUsername(), e.getMessage());
            }
        }
        log.info("{}", "AudioBookShelf监控用户状态重置结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
    }

    private void monitorUser(String username, String userId, String today) {
        if (StringUtils.isEmpty(userId)) {
            log.warn("AudioBookShelf未找到监控用户：{}", username);
            return;
        }
        AbsListeningStats listeningStats = audioBookShelfUtil.getTodayListeningStats(userId);
        Long todaySeconds = listeningStats.getToday();
        if (todaySeconds == null || todaySeconds <= 0) {
            return;
        }
        long todayMinutes = todaySeconds / 60;
        long currentTier = todayMinutes / NOTICE_TIER_MINUTES;
        String tierKey = LokTarConstant.REDIS_KEY_ABS_LISTEN_TIER_PREFIX + today + "_" + username;
        Object lastTierValue = redisUtil.get(tierKey);
        if (lastTierValue == null) {
            // 当日首次记录：以当前档位为基线不通知，避免服务重启后对当日历史时长误报
            redisUtil.set(tierKey, currentTier, TIER_RECORD_EXPIRE);
            return;
        }
        long lastTier = Long.parseLong(lastTierValue.toString());
        if (currentTier > lastTier) {
            String content = LokTarConstant.NOTICE_TITLE_ABS + System.lineSeparator() +
                    System.lineSeparator() +
                    username + " 今日收听已超 " + currentTier * NOTICE_TIER_MINUTES + " 分钟" + System.lineSeparator() +
                    listeningBookInfo(listeningStats) +
                    System.lineSeparator() +
                    DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
            redisUtil.set(tierKey, currentTier, TIER_RECORD_EXPIRE);
        }
    }

    private String listeningBookInfo(AbsListeningStats listeningStats) {
        List<AbsPlaybackSession> recentSessions = listeningStats.getRecentSessions();
        if (ObjectUtils.isEmpty(recentSessions) || StringUtils.isEmpty(recentSessions.getFirst().getDisplayTitle())) {
            return "";
        }
        return "最近收听：" + recentSessions.getFirst().getDisplayTitle() + System.lineSeparator();
    }
}
