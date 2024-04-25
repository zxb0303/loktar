package com.loktar.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.openai.OpenAiMessage;
import com.loktar.dto.openai.OpenAiRequest;
import com.loktar.dto.openai.OpenAiResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChatGPTUtil {
    public final static String BASE_URL = "https://api.openai.com";
    public final static String URL_LIST_MODELS = BASE_URL + "/v1/models";
    public final static String URL_CHAT_COMPLETIONS = BASE_URL + "/v1/chat/completions";
    public final static String GPT_MODEL_4_TURBO_PREVIEW = "gpt-4-turbo";
    public final static String ROLE_SYSTEM = "system";
    public final static String ROLE_USER = "user";
    public final static String ROLE_ASSISTANT = "assistant";
    private final static EncodingRegistry registry = Encodings.newLazyEncodingRegistry();
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final LokTarConfig lokTarConfig;

    private static final int maxPromptTokens = 3000;

    public ChatGPTUtil(LokTarConfig lokTarConfig) {
        this.lokTarConfig = lokTarConfig;
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @SneakyThrows
    public void listmodels() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_LIST_MODELS))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("Authorization", lokTarConfig.getOpenai().getApiKey())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    @SneakyThrows
    public OpenAiResponse completions(OpenAiRequest openAiRequest) {
        openAiRequest = compressPrompt(openAiRequest);
        String requestStr = objectMapper.writeValueAsString(openAiRequest);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_CHAT_COMPLETIONS))
                .timeout(Duration.ofSeconds(60))
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("Authorization", lokTarConfig.getOpenai().getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestStr))
                .build();
//        System.out.println(requestStr);
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());
        if (response.body().contains("error")) {
            return null;
        }
        return objectMapper.readValue(response.body(), OpenAiResponse.class);
    }

    @SneakyThrows
    private static OpenAiRequest compressPrompt(OpenAiRequest openAiRequest) {
        List<OpenAiMessage> messages = openAiRequest.getMessages();
        Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4);
        String promptStr = objectMapper.writeValueAsString(openAiRequest.getMessages());
        int promptTokenCount = encoding.countTokens(promptStr);
        while (promptTokenCount > maxPromptTokens) {
            messages.remove(1);
            promptStr = objectMapper.writeValueAsString(openAiRequest.getMessages());
            promptTokenCount = encoding.countTokens(promptStr);
        }
        openAiRequest.setMessages(messages);
        return openAiRequest;
    }

    public static OpenAiRequest getDefalutRequest() {
        OpenAiMessage openAiMessage = new OpenAiMessage();
        openAiMessage.setRole(ROLE_SYSTEM);
        openAiMessage.setContent("请用5周岁中国小朋友能听懂的语言进行回答");
        List<OpenAiMessage> openAiMessages = new ArrayList<>();
        openAiMessages.add(openAiMessage);
        OpenAiRequest openAiRequest = new OpenAiRequest();
        openAiRequest.setModel(GPT_MODEL_4_TURBO_PREVIEW);
        openAiRequest.setTemperature(0.8);
        openAiRequest.setStream(false);
        openAiRequest.setMaxTokens(2000);
        openAiRequest.setPresencePenalty(1.0);
        openAiRequest.setTopP(1.0);
        openAiRequest.setMessages(openAiMessages);
        return openAiRequest;
    }

}
