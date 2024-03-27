package com.loktar.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.dto.jellyfin.Session;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;


public class JellyfinUtil {

    @SneakyThrows
    public static Session getSessionByDeviceId(String deviceId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(LokTarPrivateConstant.JELLYFIN_URL, deviceId)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("x-emby-token", LokTarPrivateConstant.JELLYFIN_TOKEN)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        List<Session> sessions = objectMapper.readValue(responseBody, new TypeReference<List<Session>>(){});
        return sessions.get(0);
    }

    public static void main(String[] args) {
        System.out.println(getSessionByDeviceId("SmVsbHlmaW5NZWRpYVBsYXllciAxLjkuMSAod2luZG93cy14ODZfNjQgMTApfDE2OTMyMTUzMTMzNzU1"));
    }
}
