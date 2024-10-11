package com.loktar.web.test;

import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;

public class UnsignMain {

    @SneakyThrows
    public static void main(String[] args) {
        String[] agtIds = new String[]{"20245723067561145556"};
        for (String id: agtIds) {
            Thread.sleep(1000);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MessageFormat.format("https://discount.ttsales.cn/ttsales-discount-parking-discount-act022/afterSale/userAgt/unsign?agreementNo={0}", id)))
                    .timeout(Duration.ofSeconds(20))
                    .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        }
    }
}
