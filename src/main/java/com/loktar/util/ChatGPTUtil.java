package com.loktar.util;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.openai.OpenAiMessage;
import com.loktar.dto.openai.OpenAiRequest;
import com.loktar.dto.openai.OpenAiResponse;
import com.loktar.dto.openai.OpenAiResponseChoice;
import com.loktar.dto.openai.OpenAiResponseUsage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ChatGPTUtil {

    public static final String GPT_MODEL = "gpt-5.5";
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private final String apiKey;

    public ChatGPTUtil(LokTarConfig lokTarConfig) {
        this.apiKey = lokTarConfig.getOpenai().getApiKey();
    }

    /**
     * 调用 OpenAI Chat Completions，使用 LangChain4j 实现
     */
    public OpenAiResponse completions(OpenAiRequest openAiRequest) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(openAiRequest.getModel())
                .temperature(openAiRequest.getTemperature())
                .topP(openAiRequest.getTopP())
                .timeout(Duration.ofSeconds(60))
                .build();

        List<ChatMessage> chatMessages = convertMessages(openAiRequest.getMessages());

        try {
            ChatResponse response = model.chat(chatMessages);
            return buildResponse(openAiRequest.getModel(), response);
        } catch (Exception e) {
            log.error("OpenAI API 调用失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将自定义 OpenAiMessage 列表转换为 LangChain4j ChatMessage 列表
     */
    private List<ChatMessage> convertMessages(List<OpenAiMessage> messages) {
        return messages.stream()
                .map(m -> switch (m.getRole()) {
                    case ROLE_SYSTEM -> (ChatMessage) SystemMessage.from(m.getContent());
                    case ROLE_ASSISTANT -> (ChatMessage) AiMessage.from(m.getContent());
                    default -> (ChatMessage) UserMessage.from(m.getContent());
                })
                .toList();
    }

    /**
     * 将 LangChain4j Response 转换为自定义 OpenAiResponse
     */
    private OpenAiResponse buildResponse(String modelName, ChatResponse chatResponse) {
        OpenAiResponse openAiResponse = new OpenAiResponse();
        openAiResponse.setModel(modelName);

        OpenAiResponseChoice choice = new OpenAiResponseChoice();
        choice.setIndex(0);
        choice.setMessage(new OpenAiMessage(ROLE_ASSISTANT, chatResponse.aiMessage().text()));
        if (chatResponse.finishReason() != null) {
            choice.setFinishReason(chatResponse.finishReason().toString());
        }
        openAiResponse.setChoices(List.of(choice));

        TokenUsage tokenUsage = chatResponse.tokenUsage();
        OpenAiResponseUsage usage = new OpenAiResponseUsage();
        usage.setPromptTokens(tokenUsage.inputTokenCount());
        usage.setCompletionTokens(tokenUsage.outputTokenCount());
        usage.setTotalTokens(tokenUsage.totalTokenCount());
        openAiResponse.setUsage(usage);

        return openAiResponse;
    }

    /**
     * @deprecated 拼写错误，请使用 {@link #getDefaultRequest()}
     */
    @Deprecated
    public static OpenAiRequest getDefalutRequest() {
        return getDefaultRequest();
    }

    public static OpenAiRequest getDefaultRequest() {
        OpenAiMessage sysMsg = new OpenAiMessage(ROLE_SYSTEM, "请用5周岁中国小朋友能听懂的语言进行回答");
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(sysMsg);

        OpenAiRequest request = new OpenAiRequest();
        request.setModel(GPT_MODEL);
        request.setTemperature(1);
        request.setStream(false);
        request.setTopP(1.0);
        request.setMessages(messages);
        return request;
    }

    public static OpenAiRequest getTranslateRequest(String englishText) {
        OpenAiMessage sys = new OpenAiMessage(ROLE_SYSTEM,
                "你是一个专业的中英翻译助手。请把后面用户给出的英文内容准确翻译成通顺的简体中文，只返回翻译结果本身，不要解释，不要添加额外内容。");
        OpenAiMessage user = new OpenAiMessage(ROLE_USER, englishText);

        List<OpenAiMessage> list = new ArrayList<>();
        list.add(sys);
        list.add(user);

        OpenAiRequest req = new OpenAiRequest();
        req.setModel(GPT_MODEL);
        req.setTemperature(1);
        req.setStream(false);
        req.setTopP(1.0);
        req.setMessages(list);
        return req;
    }
}
