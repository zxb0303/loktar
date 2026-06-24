package com.loktar.util;


import lombok.extern.slf4j.Slf4j;
import com.loktar.conf.LokTarConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@Slf4j
public class HomeAssistantUtil {
    private final LokTarConfig lokTarConfig;
    private final HttpClient httpClient;

    public HomeAssistantUtil(LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
    }

    public void turnOnSwitch(String entityId) {
        sendRequest("api/services/switch/turn_on", entityId);
    }

    public void turnOffSwitch(String entityId) {
        sendRequest("api/services/switch/turn_off", entityId);
    }

    @SneakyThrows
    private void sendRequest(String action, String entityId) {
        String url = lokTarConfig.getHomeAssistant().getBaseUrl() + action;
        String requestBody = "{\"entity_id\": \"" + entityId + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + lokTarConfig.getHomeAssistant().getApiToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            log.info("{}", "Action " + action + " executed successfully for " + entityId);
        } else {
            log.error("Failed to execute action " + action + ": " + response.statusCode());
            log.error("Response: " + response.body());
        }
    }
}
