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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AudioBookShelfTask {

    // 当日累计收听每满10分钟为一个通知刻度
    private final static int NOTICE_TIER_MINUTES = 10;

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

    @Scheduled(cron = "0 */2 * * * *")
    public void listenMonitor() {
        String user = lokTarConfig.getAudioBookShelf().getUser();
        log.info("{}", "AudioBookShelf收听监控定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        AbsUser absUser = audioBookShelfUtil.getUser(user);
        if (absUser == null || !Boolean.TRUE.equals(absUser.getIsActive())) {
            //log.info("{}", "AudioBookShelf监控用户不存在或已禁用，跳过收听监控：" + user);
            return;
        }
        String today = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATE_COMPACT);
        monitorUser(user, absUser.getId(), today);
        log.info("{}", "AudioBookShelf收听监控定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
    }

    /**
     * 每天08点将监控用户状态重置为可用
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void resetUserActive() {
        String monitorUsername = lokTarConfig.getAudioBookShelf().getUser();
        AbsUser absUser = audioBookShelfUtil.getUser(monitorUsername);
        if (absUser == null || Boolean.TRUE.equals(absUser.getIsActive())) {
            return;
        }
        audioBookShelfUtil.updateUserActive(absUser.getId(), true);
    }

    private void monitorUser(String username, String userId, String today) {
        // 暂停中的会话仍保持打开，通过对比播放进度是否变化判断是否正在播放
        AbsPlaybackSession playingSession = null;
        for (AbsPlaybackSession session : audioBookShelfUtil.getOpenSessions()) {
            if (userId.equals(session.getUserId())) {
                playingSession = session;
                break;
            }
        }
        if (playingSession == null) {
            // 无打开的播放会话，未在播放
            return;
        }
        // 记录会话id与播放进度：同会话进度不变说明已暂停，会话变化说明换了内容
        String posKey = LokTarConstant.REDIS_KEY_ABS_PLAYING_POS_PREFIX + username;
        long currentTime = playingSession.getCurrentTime() == null ? 0 : playingSession.getCurrentTime();
        String currentPos = playingSession.getId() + "|" + currentTime;
        Object lastPosValue = redisUtil.get(posKey);
        if (currentPos.equals(lastPosValue)) {
            // 播放进度无变化，已暂停，不推送
            return;
        }
        AbsListeningStats listeningStats = audioBookShelfUtil.getTodayListeningStats(userId);
        Long todaySeconds = listeningStats.getToday();
        long todayMinutes = todaySeconds == null ? 0 : todaySeconds / 60;
        long currentTier = todayMinutes / NOTICE_TIER_MINUTES;
        String tierKey = LokTarConstant.REDIS_KEY_ABS_LISTEN_TIER_PREFIX + today + "_" + username;
        Object lastTierValue = redisUtil.get(tierKey);
        // 当日首次记录：以当前档位为基线不附加超时信息，避免服务重启后对当日历史时长误报
        long lastTier = lastTierValue == null ? currentTier : Long.parseLong(lastTierValue.toString());
        boolean tierReached = currentTier > lastTier;
        String content = LokTarConstant.NOTICE_TITLE_ABS + System.lineSeparator() +
                System.lineSeparator() +
                username + (tierReached ? " 今日收听已超 " + currentTier * NOTICE_TIER_MINUTES + " 分钟" : "") + System.lineSeparator() +
                "正在收听：" + playingSession.getDisplayAuthor() + " - " + playingSession.getDisplayTitle()
                + "（" + formatPlaybackTime(currentTime) + "）" + System.lineSeparator() +
                System.lineSeparator() +
                DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent010Id(), content));
        redisUtil.set(posKey, currentPos, TIER_RECORD_EXPIRE);
        if (lastTierValue == null || tierReached) {
            redisUtil.set(tierKey, currentTier, TIER_RECORD_EXPIRE);
        }
    }

    /**
     * 播放进度秒数格式化为时:分:秒
     */
    private String formatPlaybackTime(Long seconds) {
        long totalSeconds = seconds == null ? 0 : seconds;
        return String.format("%02d:%02d:%02d", totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60);
    }
}
