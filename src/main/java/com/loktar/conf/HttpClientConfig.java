package com.loktar.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 提供全局复用的 JDK HttpClient 单例。
 * 每次 HttpClient.newHttpClient() 都会启动新的 selector 线程池，开销不小，故统一注入复用。
 * 单个请求的超时建议在 HttpRequest 上通过 timeout(...) 设置。
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}
