package com.loktar.listener;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.TransmissionUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private final TransmissionUtil transmissionUtil;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;


    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, TransmissionUtil transmissionUtil, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        super(listenerContainer);
        this.transmissionUtil = transmissionUtil;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        //TODO 打印
        System.out.println("redis key过期：" + expiredKey);
        switch (expiredKey) {
            case LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET:
                transmissionUtil.altSpeedEnabled(false);
                String content = "Transmission已自动关闭限速(redis key 过期)";
                qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent002Id, content));
                break;
        }
    }
}
