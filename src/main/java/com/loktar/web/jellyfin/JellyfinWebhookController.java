package com.loktar.web.jellyfin;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.jellyfin.Notification;
import com.loktar.dto.jellyfin.Session;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.*;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("jellyfin")
public class JellyfinWebhookController {

    private final QywxApi qywxApi;
    private final TransmissionUtil transmissionUtil;
    private final JellyfinUtil jellyfinUtil;
    private final LokTarConfig lokTarConfig;
    private final RedisUtil redisUtil;
    private final IPUtil ipUtil;
    private final HomepageUtil homepageUtil;

    public JellyfinWebhookController(QywxApi qywxApi, TransmissionUtil transmissionUtil, JellyfinUtil jellyfinUtil, LokTarConfig lokTarConfig, RedisUtil redisUtil, IPUtil ipUtil, HomepageUtil homepageUtil) {
        this.qywxApi = qywxApi;
        this.transmissionUtil = transmissionUtil;
        this.jellyfinUtil = jellyfinUtil;
        this.lokTarConfig = lokTarConfig;
        this.redisUtil = redisUtil;
        this.ipUtil = ipUtil;
        this.homepageUtil = homepageUtil;
    }

    @PostMapping("/webhook")
    public void webhook(@RequestBody Notification notification) {
        HtmlEntityDecoderUtil.decodeHtmlEntities(notification);
        switch (notification.getNotificationType()) {
            case "Generic":
                handleGenericNotification(notification);
                break;
            case "PlaybackStart":
                handlePlaybackStart(notification);
                break;
            case "PlaybackStop":
                handlePlaybackStop(notification);
                break;
            default:
                break;
        }
    }

    /**
     * 处理Jellyfin通用通知，直接推送通知消息
     */
    private void handleGenericNotification(Notification notification) {
        String content = LokTarConstant.NOTICE_JELLYFIN + System.lineSeparator()
                + System.lineSeparator()
                + notification.getMessage();
        sendNotice(content);
    }

    /**
     * 处理播放开始事件：记录远程播放状态、推送播放通知、按需开启Transmission限速、切换homepage widget为NowPlaying模式
     */
    private void handlePlaybackStart(Notification notification) {
        Session session = jellyfinUtil.getSessionByDeviceId(notification.getDeviceId());
        if (!isLocalNetwork(session.getRemoteEndPoint())) {
            long expireTime = calculateSecondsDifference(notification);
            long existExpireTime = redisUtil.getExpire(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET);
            redisUtil.sSetAndTime(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET, Math.max(expireTime, existExpireTime), notification.getNotificationUsername());
        } else {
            redisUtil.setRemove(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET, notification.getNotificationUsername());
        }
        sendPlaybackNotification(notification, session, LokTarConstant.NOTICE_JELLYFIN_START);
        handleTransmissionSpeedOnStart(notification, session);
        handleHomepageWidgetOnStart(notification);
    }

    /**
     * 处理播放停止事件：清除远程播放状态、推送播放通知、按需关闭Transmission限速、无播放时切换homepage widget为Blocks模式
     */
    private void handlePlaybackStop(Notification notification) {
        Session session = jellyfinUtil.getSessionByDeviceId(notification.getDeviceId());
        redisUtil.setRemove(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET, notification.getNotificationUsername());
        sendPlaybackNotification(notification, session, LokTarConstant.NOTICE_JELLYFIN_STOP);
        handleTransmissionSpeedOnStop(notification, session);
        handleHomepageWidgetOnStop(notification);
    }

    /**
     * 组装播放事件通知内容并推送
     */
    private void sendPlaybackNotification(Notification notification, Session session, String eventType) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(eventType).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("用户：").append(notification.getNotificationUsername()).append(System.lineSeparator())
                .append("影片：").append(getPlayName(notification)).append(System.lineSeparator())
                .append("进度：").append(notification.getPlaybackPosition()).append(" / ").append(notification.getRunTime()).append(System.lineSeparator())
                .append("设备：").append(notification.getDeviceName()).append(" - ").append(notification.getClientName()).append(System.lineSeparator())
                .append("IP：").append(session.getRemoteEndPoint())
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE));
        if (!notification.getNotificationUsername().equals(LokTarConstant.JELLYFIN_NOT_NOTIFY)) {
            sendNotice(contentBuilder.toString());
        }
    }

    private String getPlayName(Notification notification) {
        if ("Movie".equals(notification.getItemType())) {
            return notification.getName() + "(" + notification.getYear() + ")";
        }
        if ("Episode".equals(notification.getItemType())) {
            return notification.getSeriesName() + "(" + notification.getYear() + ") - " +
                    "S" + notification.getSeasonNumber00() + "E" + notification.getEpisodeNumber00() + " - " +
                    notification.getName();
        }
        return "";
    }

    /**
     * 播放开始时：远程播放且未开启限速，则自动开启Transmission限速
     */
    private void handleTransmissionSpeedOnStart(Notification notification, Session session) {
        if (isLocalNetwork(session.getRemoteEndPoint())) {
            return;
        }
        if (!transmissionUtil.getSession().getArguments().getAltSpeedEnabled()) {
            transmissionUtil.altSpeedEnabled(true);
            notifyTransmissionSpeedChange(notification, "Transmission已自动开启限速");
        }
    }

    /**
     * 播放停止时：无其他远程播放且已开启限速，则自动关闭Transmission限速
     */
    private void handleTransmissionSpeedOnStop(Notification notification, Session session) {
        if (isLocalNetwork(session.getRemoteEndPoint())) {
            return;
        }
        if (redisUtil.sGetSetSize(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET) == 0 && transmissionUtil.getSession().getArguments().getAltSpeedEnabled()) {
            transmissionUtil.altSpeedEnabled(false);
            notifyTransmissionSpeedChange(notification, "Transmission已自动关闭限速");
        }
    }

    /**
     * 播放开始时：记录播放中设备（含本地播放），切换homepage widget为NowPlaying模式
     */
    private void handleHomepageWidgetOnStart(Notification notification) {
        long expireTime = calculateSecondsDifference(notification);
        long existExpireTime = redisUtil.getExpire(LokTarConstant.REDIS_KEY_JELLYFIN_PLAYING_SET);
        redisUtil.sSetAndTime(LokTarConstant.REDIS_KEY_JELLYFIN_PLAYING_SET, Math.max(expireTime, existExpireTime), notification.getDeviceId());
        homepageUtil.switchJellyfinWidgetMode(true);
    }

    /**
     * 播放停止时：移除播放中设备，无其他播放则切换homepage widget为Blocks模式
     */
    private void handleHomepageWidgetOnStop(Notification notification) {
        redisUtil.setRemove(LokTarConstant.REDIS_KEY_JELLYFIN_PLAYING_SET, notification.getDeviceId());
        if (redisUtil.sGetSetSize(LokTarConstant.REDIS_KEY_JELLYFIN_PLAYING_SET) == 0) {
            homepageUtil.switchJellyfinWidgetMode(false);
        }
    }

    /**
     * 延迟1秒后推送Transmission限速变更结果
     */
    private void notifyTransmissionSpeedChange(Notification notification, String content) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        if (!notification.getNotificationUsername().equals(LokTarConstant.JELLYFIN_NOT_NOTIFY)) {
            sendNotice(content);
        }
    }

    /**
     * 推送企业微信通知
     */
    private void sendNotice(String content) {
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent004Id(), content));
    }

    private static long calculateSecondsDifference(Notification notification) {
        // 将字符串解析为LocalTime对象
        LocalTime startTime = LocalTime.parse(notification.getPlaybackPosition());
        LocalTime endTime = LocalTime.parse(notification.getRunTime());
        // 计算两个时间之间的差异，以秒为单位
        return ChronoUnit.SECONDS.between(startTime, endTime);
    }

    @SneakyThrows
    private boolean isLocalNetwork(String remoteEndPoint) {
        String ipAddress = remoteEndPoint.split(":")[0];
        InetAddress address = InetAddress.getByName(ipAddress);
        String ip = ipUtil.getip();

        if (remoteEndPoint.equals(ip)) {
            return true;
        }

        // 检查是否为回环地址
        if (address.isLoopbackAddress()) {
            return true;
        }

        // 转换为字节形式
        byte[] bytes = address.getAddress();

        // 检查是否为私有地址
        // 10.x.x.x
        if ((bytes[0] & 0xFF) == 10) {
            return true;
        }
        // 172.16.x.x - 172.31.x.x
        if (((bytes[0] & 0xFF) == 172) && ((bytes[1] & 0xF0) == 16)) {
            return true;
        }
        // 192.168.x.x
        if (((bytes[0] & 0xFF) == 192) && ((bytes[1] & 0xFF) == 168)) {
            return true;
        }

        return false;
    }
}
