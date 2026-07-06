package com.loktar.util;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.common.IntentResult;
import com.loktar.service.common.NoticeServer;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ChatGPTUtil {

    public static final String GPT_MODEL = "gpt-5.5";

    private static final int CHAT_MEMORY_MAX_MESSAGES = 20;

    private final String apiKey;
    private final NoticeServer noticeServer;
    private final RedisChatMemoryStore redisChatMemoryStore;

    public ChatGPTUtil(LokTarConfig lokTarConfig, NoticeServer noticeServer, RedisChatMemoryStore redisChatMemoryStore) {
        this.apiKey = lokTarConfig.getOpenai().getApiKey();
        this.noticeServer = noticeServer;
        this.redisChatMemoryStore = redisChatMemoryStore;
    }

    /**
     * 基于 ChatMemory 进行单轮对话，自动维护会话上下文。
     * system prompt 每次前置传入，不存入 ChatMemory，避免被消息窗口淘汰。
     */
    public ChatResponse chat(String userId, String message, String model) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(userId)
                .maxMessages(CHAT_MEMORY_MAX_MESSAGES)
                .chatMemoryStore(redisChatMemoryStore)
                .build();

        List<ChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(dev.langchain4j.data.message.SystemMessage.from("请用8周岁中国小朋友能听懂的语言进行回答。不要回复表情、markdown、特殊符号等无法朗读的内容。"));
        requestMessages.addAll(chatMemory.messages());
        requestMessages.add(dev.langchain4j.data.message.UserMessage.from(message));

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(1.0)
                .topP(1.0)
                .timeout(Duration.ofSeconds(60))
                .build();

        try {
            ChatResponse response = chatModel.chat(requestMessages);
            chatMemory.add(dev.langchain4j.data.message.UserMessage.from(message));
            chatMemory.add(AiMessage.from(response.aiMessage().text()));
            return response;
        } catch (Exception e) {
            log.error("OpenAI API 调用失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 重置指定用户的会话缓存。
     */
    public void resetChat(String userId) {
        redisChatMemoryStore.deleteMessages(userId);
    }

    private static IntentResult buildNotNoticeResult() {
        IntentResult result = new IntentResult();
        result.setNotice(false);
        result.setNeedConfirm(false);
        return result;
    }

    /**
     * 使用 LangChain4j Tool/Function Calling 识别用户消息是否为添加提醒意图
     */
    public IntentResult extractIntent(String userMessage, IntentResult draft, String modelName, String noticeUser) {
        IntentTool intentTool = new IntentTool(noticeServer, noticeUser);
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(1.0)
                .timeout(Duration.ofSeconds(60))
                .build();
        String currentTime = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
        String draftContext = buildDraftContext(draft);
        IntentAssistant assistant = AiServices.builder(IntentAssistant.class)
                .chatModel(model)
                .tools(intentTool)
                .build();
        assistant.analyze(currentTime, draftContext, userMessage);
        IntentResult result = intentTool.getResult();
        return result != null ? result : buildNotNoticeResult();
    }

    private static String buildDraftContext(IntentResult draft) {
        if (draft == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("这是用户的后续补充消息，请结合之前已收集的提醒信息一起判断：\n");
        if (StringUtils.isNotBlank(draft.getTitle())) {
            sb.append("- 已确定的提醒标题：").append(draft.getTitle()).append("\n");
        }
        if (StringUtils.isNotBlank(draft.getContent())) {
            sb.append("- 已确定的提醒内容：").append(draft.getContent()).append("\n");
        }
        if (StringUtils.isNotBlank(draft.getNoticeTime())) {
            sb.append("- 已确定的提醒时间：").append(draft.getNoticeTime()).append("\n");
        }
        if (StringUtils.isNotBlank(draft.getUncertainField())) {
            sb.append("- 还缺少的信息：").append(draft.getUncertainField()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 当提醒信息不完整时，让 AI 用自然亲切的语气询问用户补充缺失信息。
     */
    public String buildNoticeConfirmReply(IntentResult draft, String modelName) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(1.0)
                .timeout(Duration.ofSeconds(60))
                .build();
        String currentTime = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
        String prompt = """
                用户想要添加一条提醒，但目前还缺少一些必要信息。
                请用自然、亲切的语气（像朋友一样）询问用户补充缺少的信息，不要透露任何系统内部判断或工具调用。
                当前时间：%s

                已收集的提醒信息：
                - 提醒标题：%s
                - 提醒内容：%s
                - 提醒时间：%s
                - 还缺少的信息：%s

                请只返回一句自然的询问，例如"明天几点提醒你？"、"方便告诉我具体是明天几点吗？"等。不要添加解释、不要加前缀、不要表情和markdown符号。
                """.formatted(
                currentTime,
                StringUtils.defaultString(draft.getTitle(), "未确定"),
                StringUtils.defaultString(draft.getContent(), "未确定"),
                StringUtils.defaultString(draft.getNoticeTime(), "未确定"),
                StringUtils.defaultString(draft.getUncertainField(), "缺少必要信息")
        );
        ChatResponse response = model.chat(dev.langchain4j.data.message.UserMessage.from(prompt));
        return response.aiMessage().text();
    }

    interface IntentAssistant {
        @SystemMessage("""
                你是一个会话意图识别助手。请判断用户输入的意图，并根据情况调用对应的工具：
                - addNotice：用户想要添加一条提醒/备忘/待办，且已经提供了具体提醒时间、标题等所有必要信息
                - askForMissingInfo：用户想要添加提醒但缺少必要信息（特别是具体提醒时间）
                - resetSession：用户明确说"重置会话"、"清空会话"、"清空上下文"、"重置聊天"等，想要清空当前对话上下文
                如果用户输入不属于以上任何一种意图，不要调用任何工具。
                注意：你的回复最终会通过文本转语音播报，所以不要包含表情、markdown、特殊符号等无法朗读的内容。
                当前时间：{{currentTime}}
                {{draftContext}}""")
        void analyze(@V("currentTime") String currentTime, @V("draftContext") String draftContext, @UserMessage String userMessage);
    }

    interface TranslatorAssistant {
        @SystemMessage("你是一个专业的中英翻译助手。请把后面用户给出的英文内容准确翻译成通顺的简体中文，只返回翻译结果本身，不要解释，不要添加额外内容，不要表情和markdown符号。")
        String translate(@UserMessage String englishText);
    }
}
