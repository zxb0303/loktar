package com.loktar.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class IPUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final LokTarConfig lokTarConfig;
    private final HttpClient httpClient;

    public IPUtil(LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
    }

    @SneakyThrows
    public String getip() {
//        HttpClient httpClient = HttpClient.newHttpClient();
//                .uri(URI.create(MessageFormat.format("http://api.ipstack.com/check?access_key={0}", lokTarConfig.getIpstack().getAccessKey())))
//                .timeout(Duration.ofSeconds(10))
//                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
//                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
//                .GET()
//                .build();
//        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//        String responseBody = response.body();
//        ObjectNode objectNode = (ObjectNode) objectMapper.readTree(responseBody);
//        return objectNode.get("ip").asText();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ip.sb/ip"))
                .timeout(Duration.ofSeconds(60))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body().trim();
        return responseBody;
    }

}
