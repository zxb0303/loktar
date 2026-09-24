package com.loktar.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.bandwagonhost.VPSInfo;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;

@Component
public class BandwagonhostUtil {

    private final static String URL = "https://api.64clouds.com/v1/getServiceInfo?veid={0}&&api_key={1}";
    private static final JsonMapper objectMapper = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .changeDefaultPropertyInclusion(inclusion -> inclusion
                    .withValueInclusion(JsonInclude.Include.NON_NULL)
                    .withContentInclusion(JsonInclude.Include.NON_NULL))
            .build();
    private final HttpClient httpClient;

    public BandwagonhostUtil(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @SneakyThrows
    public VPSInfo getVPSData(String veid, String apiKey) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL, veid, apiKey)))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), VPSInfo.class);
    }
}
