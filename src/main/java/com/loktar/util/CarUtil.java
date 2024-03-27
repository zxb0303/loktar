package com.loktar.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CarUtil {
    private final static String URL = "https://support.volvo.care/v1/quality-info/release-notes/cn/V526/23w17";

    @SneakyThrows
    public static String getLastVersion() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("Article-Content-Type", "JSON")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonObject = (JsonNode) objectMapper.readTree(response.body());
        JsonNode jsonContent = jsonObject.get("jsonContent");
        ArrayNode jsonArrayBody = (ArrayNode) jsonContent.get("body");
        JsonNode lastVersionObject = jsonArrayBody.get(1);
        ArrayNode jsonArrayChildren = (ArrayNode) lastVersionObject.get("children");
        JsonNode jsonArrayChildren1 = jsonArrayChildren.get(0);
        String lastVersion = jsonArrayChildren1.get("children").asText().replace("软件", "").replace("更新", "");
        return lastVersion;
    }

    public static void main(String[] args) {
        System.out.println(getLastVersion());
    }
}
