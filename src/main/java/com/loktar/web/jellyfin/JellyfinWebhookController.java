package com.loktar.web.jellyfin;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.jellyfin.Notification;
import com.loktar.dto.jellyfin.Session;
import com.loktar.dto.transmission.TrResponse;
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

@RestController
@RequestMapping("jellyfin")
public class JellyfinWebhookController {

    private final QywxApi qywxApi;
    private final TransmissionUtil transmissionUtil;
    private final JellyfinUtil jellyfinUtil;
    private final LokTarConfig lokTarConfig;
    private final RedisUtil redisUtil;
    private final IPUtil ipUtil;

    public JellyfinWebhookController(QywxApi qywxApi, TransmissionUtil transmissionUtil, JellyfinUtil jellyfinUtil, LokTarConfig lokTarConfig, RedisUtil redisUtil, IPUtil ipUtil) {
        this.qywxApi = qywxApi;
        this.transmissionUtil = transmissionUtil;
        this.jellyfinUtil = jellyfinUtil;
        this.lokTarConfig = lokTarConfig;
        this.redisUtil = redisUtil;
        this.ipUtil = ipUtil;
    }

    @PostMapping("/webhook.do")
    public void webhook(@RequestBody Notification notification) {
        HtmlEntityDecoderUtil.decodeHtmlEntities(notification);
        StringBuilder contentBuilder = new StringBuilder();
        switch (notification.getNotificationType()) {
            case "Generic":
                contentBuilder.append(LokTarConstant.NOTICE_JELLYFIN).append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(notification.getMessage());
                qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent004Id(), contentBuilder.toString()));
                break;
            case "PlaybackStart":
            case "PlaybackStop":
                Session session = jellyfinUtil.getSessionByDeviceId(notification.getDeviceId());
                handlePlaybackEvents(notification, session, contentBuilder);
                contentBuilder.append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE));
                if (!notification.getNotificationUsername().equals(LokTarConstant.JELLYFIN_NOT_NOTIFY)) {
                    qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent004Id(), contentBuilder.toString()));
                }
                handleTransmissionSpeed(notification, session);
                break;
            default:
                break;
        }

    }

    private void handlePlaybackEvents(Notification notification, Session session, StringBuilder contentBuilder) {
        String playName = getPlayName(notification);
        String eventType = "PlaybackStart".equals(notification.getNotificationType()) ? LokTarConstant.NOTICE_JELLYFIN_START : LokTarConstant.NOTICE_JELLYFIN_STOP;

        if ("PlaybackStart".equals(notification.getNotificationType()) && !isLocalNetwork(session.getRemoteEndPoint())) {
            long expireTime = calculateSecondsDifference(notification);
            long existExpireTime = redisUtil.getExpire(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET);
            expireTime = Math.max(expireTime, existExpireTime);
            redisUtil.sSetAndTime(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET, expireTime, notification.getNotificationUsername());
        } else {
            redisUtil.setRemove(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET, notification.getNotificationUsername());
        }

        contentBuilder.append(eventType).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("用户：").append(notification.getNotificationUsername()).append(System.lineSeparator())
                .append("影片：").append(playName).append(System.lineSeparator())
                .append("进度：").append(notification.getPlaybackPosition()).append(" / ").append(notification.getRunTime()).append(System.lineSeparator())
                .append("设备：").append(notification.getDeviceName()).append(" - ").append(notification.getClientName()).append(System.lineSeparator())
                .append("IP：").append(session.getRemoteEndPoint());
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

    private void handleTransmissionSpeed(Notification notification, Session session) {
        if (!isLocalNetwork(session.getRemoteEndPoint())) {
            String content = "";
            TrResponse trResponseSession = transmissionUtil.getSession();
            if ("PlaybackStart".equals(notification.getNotificationType()) && !trResponseSession.getArguments().getAltSpeedEnabled()) {
                transmissionUtil.altSpeedEnabled(true);
                content = "Transmission已自动开启限速";
            }
            if ("PlaybackStop".equals(notification.getNotificationType()) && redisUtil.sGetSetSize(LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET) == 0 && trResponseSession.getArguments().getAltSpeedEnabled()) {
                transmissionUtil.altSpeedEnabled(false);
                content = "Transmission已自动关闭限速";
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (!notification.getNotificationUsername().equals(LokTarConstant.JELLYFIN_NOT_NOTIFY)) {
                qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent004Id(), content));
            }
        }
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
