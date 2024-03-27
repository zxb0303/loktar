package com.loktar.learn.jdk11;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.github.GithubRelease;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Http {
    public static void main(String[] args) throws Exception {
        //异步请求示例
        //test1();
        //同步请求示例
        //test2();
        //url拼接、header、忽略未识别的属性、对象接收、驼峰转换
        test3();
        //POST请求示例
        //TransmissionUtil.realTrPRC(null);


    }
    @SneakyThrows
    private static void test3() {
        String registory="gethomepage/homepage";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format("https://api.github.com/repos/{0}/releases", registory)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        List<GithubRelease> githubReleases = objectMapper.readValue(response.body(), new TypeReference<List<GithubRelease>>(){});
        for (GithubRelease githubRelease : githubReleases) {
            if (!githubRelease.isPrerelease()) {
               System.out.println(githubRelease.toString());
            }
        }
    }

    @SneakyThrows
    private static void test2() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/data"))
                .build();
        //同步请求
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();
        System.out.println("Status code: " + statusCode);
        System.out.println("Response body: " + responseBody);
    }

    private static void test1() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/data"))
                .build();
        // 发送HTTP请求并异步获取响应
        CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        // 处理响应
        future.thenAccept(response -> {
            int statusCode = response.statusCode();
            String responseBody = response.body();
            System.out.println("Status code: " + statusCode);
            System.out.println("Response body: " + responseBody);
        });
        // 等待异步请求完成
        future.join();
    }
}
