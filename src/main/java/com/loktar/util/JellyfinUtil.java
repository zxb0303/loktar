package com.loktar.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.jellyfin.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class JellyfinUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final LokTarConfig lokTarConfig;
    private final HttpClient httpClient;

    public JellyfinUtil(LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @SneakyThrows
    public Session getSessionByDeviceId(String deviceId) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(lokTarConfig.getJellyfin().getUrl(), deviceId)))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_AUTHORIZATION_NAME, "MediaBrowser Token=\"" + lokTarConfig.getJellyfin().getToken() + "\"")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            log.error("Jellyfin Sessions接口调用失败, deviceId:{}, statusCode:{}, body:{}", deviceId, response.statusCode(), response.body());
            throw new IllegalStateException("Jellyfin Sessions接口调用失败, statusCode:" + response.statusCode());
        }
        String responseBody = response.body();
        List<Session> sessions = objectMapper.readValue(responseBody, new TypeReference<>(){});
        if (sessions.isEmpty()) {
            log.warn("Jellyfin Sessions接口未查询到会话, deviceId:{}", deviceId);
            throw new IllegalStateException("Jellyfin Sessions接口未查询到会话, deviceId:" + deviceId);
        }
        return sessions.getFirst();
    }
}
