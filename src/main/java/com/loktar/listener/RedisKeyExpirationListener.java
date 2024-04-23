package com.loktar.listener;


import com.loktar.conf.LokTarConstant;
import com.loktar.util.TransmissionUtil;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private final TransmissionUtil transmissionUtil;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, TransmissionUtil transmissionUtil) {
        super(listenerContainer);
        this.transmissionUtil = transmissionUtil;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        System.out.println("redis key过期：" + expiredKey);
        switch (expiredKey) {
            case LokTarConstant.REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET:
                transmissionUtil.altSpeedEnabled(false);
                break;
        }
    }
}
