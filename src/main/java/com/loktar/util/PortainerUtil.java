package com.loktar.util;


import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loktar.conf.LokTarConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@Slf4j
public class PortainerUtil {

    private final LokTarConfig lokTarConfig;

    private final HttpClient httpClient;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final static String HEADER_API_KEY = "X-API-Key";

    public PortainerUtil(LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SneakyThrows
    public boolean restartContainerByName(String containerName) {
        String baseUrl = lokTarConfig.getPortainer().getBaseUrl();
        String endpointId = lokTarConfig.getPortainer().getEndpointId();
        String apiToken = lokTarConfig.getPortainer().getApiToken();
        ObjectNode filtersNode = objectMapper.createObjectNode();
        filtersNode.putArray("name").add("/" + containerName);
        String filters = URLEncoder.encode(objectMapper.writeValueAsString(filtersNode), StandardCharsets.UTF_8);
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/endpoints/" + endpointId + "/docker/containers/json?all=true&filters=" + filters))
                .timeout(Duration.ofSeconds(30))
                .header(HEADER_API_KEY, apiToken)
                .GET()
                .build();
        HttpResponse<String> listResponse = httpClient.send(listRequest, HttpResponse.BodyHandlers.ofString());
        if (listResponse.statusCode() < 200 || listResponse.statusCode() >= 300) {
            log.error("{}", "Portainer获取容器列表失败：" + containerName + "，响应码：" + listResponse.statusCode());
            return false;
        }
        JsonNode containers = objectMapper.readTree(listResponse.body());
        String containerId = null;
        for (JsonNode container : containers) {
            JsonNode names = container.get("Names");
            if (names != null && names.isArray()) {
                for (JsonNode name : names) {
                    if (("/" + containerName).equals(name.asText())) {
                        containerId = container.get("Id").asText();
                        break;
                    }
                }
            }
            if (containerId != null) {
                break;
            }
        }
        if (containerId == null) {
            log.warn("{}", "Portainer中未找到容器：" + containerName);
            return false;
        }
        HttpRequest restartRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/endpoints/" + endpointId + "/docker/containers/" + containerId + "/restart"))
                .timeout(Duration.ofSeconds(30))
                .header(HEADER_API_KEY, apiToken)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> restartResponse = httpClient.send(restartRequest, HttpResponse.BodyHandlers.ofString());
        if (restartResponse.statusCode() >= 200 && restartResponse.statusCode() < 300) {
            log.info("{}", "容器重启成功：" + containerName);
            return true;
        } else {
            log.error("{}", "容器重启失败：" + containerName + "，响应码：" + restartResponse.statusCode());
            return false;
        }
    }
}
