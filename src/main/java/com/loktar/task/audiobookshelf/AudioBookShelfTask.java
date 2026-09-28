package com.loktar.task.audiobookshelf;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.audiobookshelf.AbsListeningStats;
import com.loktar.dto.audiobookshelf.AbsPlaybackSession;
import com.loktar.dto.audiobookshelf.AbsUser;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.AudioBookShelfUtil;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Slf4j
public class AudioBookShelfTask {

    // 当日累计收听每满10分钟为一个通知刻度，到达新刻度即推送通知
    private final static int NOTICE_TIER_MINUTES = 10;

    // 当日累计收听每满20分钟为一个自动关闭刻度，到达新刻度即把用户置为不可用
    private final static int AUTO_CLOSE_TIER_MINUTES = 20;

    // Redis刻度记录保留2天，避免历史数据堆积
    private final static Duration TIER_RECORD_EXPIRE = Duration.ofDays(2);

    private final LokTarConfig lokTarConfig;

    private final RedisTemplate<String, Object> redisTemplate;

    private final QywxApi qywxApi;

    private final AudioBookShelfUtil audioBookShelfUtil;

    public AudioBookShelfTask(LokTarConfig lokTarConfig, RedisTemplate<String, Object> redisTemplate, QywxApi qywxApi, AudioBookShelfUtil audioBookShelfUtil) {
        this.lokTarConfig = lokTarConfig;
        this.redisTemplate = redisTemplate;
        this.qywxApi = qywxApi;
        this.audioBookShelfUtil = audioBookShelfUtil;
    }

    // 每2分钟监控
    @Scheduled(cron = "0 */2 7-22 * * *")
    public void listenMonitor() {
        String user = lokTarConfig.getAudioBookShelf().getUser();
        log.info("{}", "AudioBookShelf收听监控定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        AbsUser absUser = audioBookShelfUtil.getUser(user);
        if (absUser == null || !Boolean.TRUE.equals(absUser.getIsActive())) {
            log.info("{}", "AudioBookShelf监控用户已禁用，跳过收听监控：" + user);
            return;
        }
        String today = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATE_COMPACT);
        monitorUser(user, absUser.getId(), today);
        log.info("{}", "AudioBookShelf收听监控定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
    }

    /**
     * 每天07点将监控用户状态重置为可用
     */
    @Scheduled(cron = "0 0 7 * * *")
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
        AbsPlaybackSession openSession = findOpenSession(userId);
        if (openSession == null || isPaused(username, openSession)) {
            return;
        }
        long todayMinutes = getTodayListeningMinutes(userId);
        long noticeTier = todayMinutes / NOTICE_TIER_MINUTES;
        long closeTier = todayMinutes / AUTO_CLOSE_TIER_MINUTES;
        String noticeTierKey = LokTarConstant.REDIS_KEY_ABS_LISTEN_TIER_PREFIX + today + "_" + username;
        String closeTierKey = LokTarConstant.REDIS_KEY_ABS_CLOSE_TIER_PREFIX + today + "_" + username;
        boolean noticeReached = isNewTierReached(noticeTierKey, noticeTier);
        boolean closeReached = isNewTierReached(closeTierKey, closeTier);
        // 通知刻度：推送收听通知（当日累计时长与正在收听内容）
        if (noticeReached) {
            sendListeningNotice(username, openSession, noticeTier * NOTICE_TIER_MINUTES);
        }
        // 关闭刻度：把用户置为不可用即停止其播放并推送关闭通知，当天剩余轮次监控直接跳过，次日07点由resetUserActive恢复
        if (closeReached) {
            audioBookShelfUtil.updateUserActive(userId, false);
            sendAutoCloseNotice(username);
        }
        // 先动作后落库：动作失败时本轮进度与档位不记录，下轮重新判定实现幂等重试
        recordPlayingPos(username, openSession);
        recordTier(noticeTierKey, noticeTier);
        recordTier(closeTierKey, closeTier);
    }

    /**
     * 查找用户的打开播放会话，未找到说明未在播放
     */
    private AbsPlaybackSession findOpenSession(String userId) {
        for (AbsPlaybackSession session : audioBookShelfUtil.getOpenSessions()) {
            if (userId.equals(session.getUserId())) {
                return session;
            }
        }
        return null;
    }

    /**
     * 判断是否已暂停：播放进度标识与上轮记录一致即已暂停
     */
    private boolean isPaused(String username, AbsPlaybackSession session) {
        Object lastPosValue = redisTemplate.opsForValue().get(LokTarConstant.REDIS_KEY_ABS_PLAYING_POS_PREFIX + username);
        return playingPos(session).equals(lastPosValue);
    }

    /**
     * 记录播放进度标识，供下轮暂停判断
     */
    private void recordPlayingPos(String username, AbsPlaybackSession session) {
        redisTemplate.opsForValue().set(LokTarConstant.REDIS_KEY_ABS_PLAYING_POS_PREFIX + username, playingPos(session), TIER_RECORD_EXPIRE);
    }

    /**
     * 播放进度标识：会话id|进度秒数，同会话进度不变说明已暂停，会话变化说明换了内容
     */
    private String playingPos(AbsPlaybackSession session) {
        long currentTime = session.getCurrentTime() == null ? 0 : session.getCurrentTime();
        return session.getId() + "|" + currentTime;
    }

    /**
     * 当日累计收听分钟数（listening-stats的today为当日收听秒数）
     */
    private long getTodayListeningMinutes(String userId) {
        AbsListeningStats listeningStats = audioBookShelfUtil.getTodayListeningStats(userId);
        Long todaySeconds = listeningStats.getToday();
        return todaySeconds == null ? 0 : todaySeconds / 60;
    }

    /**
     * 判断是否到达新档位：当日首次记录（Redis无历史值）以当前档位为基线不算到达，
     * 避免服务重启后对当日历史时长误报；此后档位提升才算到达
     */
    private boolean isNewTierReached(String tierKey, long tier) {
        Object lastTierValue = redisTemplate.opsForValue().get(tierKey);
        long lastTier = lastTierValue == null ? tier : Long.parseLong(lastTierValue.toString());
        return tier > lastTier;
    }

    /**
     * 记录档位（当日首次记录即基线），保留2天避免历史数据堆积
     */
    private void recordTier(String tierKey, long tier) {
        redisTemplate.opsForValue().set(tierKey, tier, TIER_RECORD_EXPIRE);
    }

    /**
     * 推送通知刻度通知：当日累计收听时长与正在收听内容
     */
    private void sendListeningNotice(String username, AbsPlaybackSession playingSession, long noticeMinutes) {
        String content = username + " 今日收听已超 " + noticeMinutes + " 分钟" + System.lineSeparator() +
                "正在收听：" + playingSession.getDisplayAuthor() + " - " + playingSession.getDisplayTitle()
                + "（" + formatPlaybackTime(playingSession.getCurrentTime()) + "）";
        sendNotice(buildNoticeContent(content));
    }

    /**
     * 推送自动关闭通知：收听时长与内容已由同时刻的收听通知携带，仅提示已关闭
     */
    private void sendAutoCloseNotice(String username) {
        sendNotice(buildNoticeContent(username + " 已自动关闭"));
    }

    /**
     * 构建通知内容：标题 + 内容 + 发送时间
     */
    private String buildNoticeContent(String content) {
        return LokTarConstant.NOTICE_TITLE_ABS + System.lineSeparator() +
                System.lineSeparator() +
                content + System.lineSeparator() +
                System.lineSeparator() +
                DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
    }

    /**
     * 推送企业微信文本通知
     */
    private void sendNotice(String content) {
        qywxApi.sendTextMsg(new AgentMsgText(LokTarConstant.QYWX_NOTICE_ALL, lokTarConfig.getQywx().getAgent010Id(), content));
    }

    /**
     * 播放进度秒数格式化为时:分:秒
     */
    private String formatPlaybackTime(Long seconds) {
        long totalSeconds = seconds == null ? 0 : seconds;
        return String.format("%02d:%02d:%02d", totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60);
    }
}
