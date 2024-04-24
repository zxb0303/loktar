package com.loktar.util;


import com.loktar.conf.LokTarConstant;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;

public class HZnotaryUtil {

    public final static String TYPE_REGIST = "登记";
    public final static String TYPE_RESULT = "摇号结果";

    public final static String URL_INDEX = "https://www.hz-notary.com/lottery/index?page.pageNum={0}";
    public final static String URL_DETAIL = "https://www.hz-notary.com/lottery/detail?lottery.id={0}";
    public final static String URL_DETAIL_CONTENT = "https://www.hz-notary.com/lottery/detail_content?lotteryContent.id={0}";

    @SneakyThrows
    private static String getLotteryId(String houseName) {
        int i = 1;
        while (i > 0) {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MessageFormat.format(URL_INDEX, String.valueOf(i))))
                    .timeout(Duration.ofSeconds(10))
                    .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                    .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            Document document = Jsoup.parse(response.body());
            List<Element> houseAtags = document.selectFirst("[class=data-list]").select("a");
            for (Element houseAtag : houseAtags) {
                if (houseAtag.html().trim().equals(houseName)) {
                    String href = houseAtag.attr("href");
                    return href.split("=")[1];
                }
            }
            i = i + 1;
        }
        return null;
    }

    @SneakyThrows
    private static String getLotteryContentId(String lotteryId, String type) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_DETAIL, lotteryId)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        Document document = Jsoup.parse(response.body());
        List<Element> houseAtags = document.selectFirst("[class=data-list]").select("a");
        //System.out.println(houseAtags.toString());
        for (Element houseAtag : houseAtags) {
            if (houseAtag.html().trim().contains(type)) {
                String href = houseAtag.attr("href");
                return href.split("=")[1];
            }
        }
        return null;
    }

    @SneakyThrows
    private static String getPdfUrl(String lotteryContentId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_DETAIL_CONTENT, lotteryContentId)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        Document document = Jsoup.parse(response.body());
        Element Atag = document.selectFirst("[class=detail_content]").selectFirst("a");
        return Atag.attr("href");
    }

    /**
     * @param houseName
     * @param type      登记,结果
     * @return
     */
    public static String getPDFUrlByHouseNameAndType(String houseName, String type) {
        String lotteryId = getLotteryId(houseName);
        String lotteryContentId = getLotteryContentId(lotteryId, type);
        return getPdfUrl(lotteryContentId);
    }
}
