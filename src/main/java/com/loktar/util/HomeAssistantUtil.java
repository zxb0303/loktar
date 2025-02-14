package com.loktar.util;

import com.loktar.conf.LokTarConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class HomeAssistantUtil {
    private final LokTarConfig lokTarConfig;

    public HomeAssistantUtil(LokTarConfig lokTarConfig) {
        this.lokTarConfig = lokTarConfig;
    }

    public void turnOnSwitch(String entityId) {
        sendRequest("api/services/switch/turn_on", entityId);
    }

    public void turnOffSwitch(String entityId) {
        sendRequest("api/services/switch/turn_off", entityId);
    }

    @SneakyThrows
    private void sendRequest(String action, String entityId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = lokTarConfig.getHomeAssistant().getBaseUrl() + action;
        String requestBody = "{\"entity_id\": \"" + entityId + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + lokTarConfig.getHomeAssistant().getApiToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Action " + action + " executed successfully for " + entityId);
        } else {
            System.err.println("Failed to execute action " + action + ": " + response.statusCode());
            System.err.println("Response: " + response.body());
        }
    }
}
