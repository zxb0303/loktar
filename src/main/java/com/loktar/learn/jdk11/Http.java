package com.loktar.learn.jdk11;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Http {
    @SneakyThrows
    public static void main(String[] args)  {
        //异步请求示例
        test1();
        //同步请求示例
        //test2();
        //POST请求示例
        //TransmissionUtil.realTrPRC(null);
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
