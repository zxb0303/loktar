package com.loktar.util;

import com.loktar.conf.LokTarConstant;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageJsonCodec;
import dev.langchain4j.data.message.JacksonChatMessageJsonCodec;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    private final StringRedisTemplate stringRedisTemplate;
    private final ChatMessageJsonCodec chatMessageJsonCodec;

    public RedisChatMemoryStore(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.chatMessageJsonCodec = new JacksonChatMessageJsonCodec();
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = stringRedisTemplate.opsForValue().get(buildKey(memoryId));
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return chatMessageJsonCodec.messagesFromJson(json);
        } catch (RuntimeException e) {
            log.warn("Chat memory JSON is corrupted or incompatible, resetting. memoryId={}, key={}, cause={}", memoryId, buildKey(memoryId), e.getMessage());
            stringRedisTemplate.delete(buildKey(memoryId));
            return new ArrayList<>();
        }
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        stringRedisTemplate.opsForValue().set(buildKey(memoryId), chatMessageJsonCodec.messagesToJson(messages), Duration.ofSeconds(3600));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        stringRedisTemplate.delete(buildKey(memoryId));
    }

    private String buildKey(Object memoryId) {
        return LokTarConstant.REDIS_KEY_PREFIX_CHAT_MEMORY + memoryId;
    }
}
