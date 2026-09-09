package com.loktar.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
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
        // 查询失败多为瞬时故障（如SSL握手被远端终止），间隔重试兜底；重试耗尽抛出异常终止本轮任务，下轮调度自然补偿
        int maxAttempts = 3;
        IOException lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return response.body().trim();
            } catch (IOException e) {
                lastException = e;
                log.warn("公网IP查询失败，第 {}/{} 次尝试：{}", attempt, maxAttempts, e.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                }
            }
        }
        throw lastException;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        IPUtil ipUtil = new IPUtil(null, HttpClient.newBuilder().build());
        String ip = ipUtil.getip();
        System.out.println(ip);
    }
}
