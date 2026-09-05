package com.loktar.util;



import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.transmission.TrRss;
import com.loktar.domain.transmission.TrRssTorrent;
import com.loktar.dto.rss.RssFeed;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RssUtil {

    public final static ObjectMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final HttpClient httpClient;

    public RssUtil(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private final static int RSS_FETCH_MAX_ATTEMPTS = 3;

    @SneakyThrows
    public List<TrRssTorrent> getRssData(TrRss trRss) {
        List<TrRssTorrent> trRssTorrents = new ArrayList<>();
        String rssBody = fetchRssWithRetry(trRss.getRssUrl());
        RssFeed rssFeed = xmlMapper.readValue(rssBody, RssFeed.class);
        rssFeed.getChannel().getItem().forEach(item -> {
            TrRssTorrent trRssTorrent = new TrRssTorrent();
            trRssTorrent.setRssId(trRss.getRssId());
            trRssTorrent.setTitle(item.getTitle());
            trRssTorrent.setLink(item.getLink());
            String idStr = item.getLink().split("id=")[1];
            if (idStr.contains("&")) {
                idStr = idStr.split("&")[0];
            }
            int id = Integer.parseInt(idStr);
            trRssTorrent.setRssTorrentId(id);
            if(!ObjectUtils.isEmpty(item.getEnclosure())){
                trRssTorrent.setDownloadUrl(item.getEnclosure().getUrl());
            }else{
                trRssTorrent.setDownloadUrl(item.getLink());
            }
            ZonedDateTime localZonedDateTime = ZonedDateTime.parse(item.getPubDate(), DateTimeUtil.FORMATTER_RSS_ITEM_PUB).withZoneSameInstant(ZoneId.systemDefault());
            LocalDateTime localDateTime = localZonedDateTime.toLocalDateTime();
            trRssTorrent.setPubDate(localDateTime);
            trRssTorrent.setStatus(0);
            trRssTorrents.add(trRssTorrent);
        });

        return trRssTorrents;
    }

    /**
     * 抓取 RSS，超时/网络异常时重试（HttpTimeoutException 是 IOException 的子类）
     */
    private String fetchRssWithRetry(String rssUrl) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(rssUrl))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .GET()
                .build();
        IOException lastException = null;
        for (int attempt = 1; attempt <= RSS_FETCH_MAX_ATTEMPTS; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException e) {
                lastException = e;
                log.warn("RSS抓取失败，第 {}/{} 次尝试：{}，url：{}", attempt, RSS_FETCH_MAX_ATTEMPTS, e.getMessage(), rssUrl);
                if (attempt < RSS_FETCH_MAX_ATTEMPTS) {
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

    public static void main(String[] args)  {
        TrRss trRss = new TrRss();
        trRss.setRssId(111);
        trRss.setRssUrl("");
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        List<TrRssTorrent> trRssTorrents = new RssUtil(httpClient).getRssData(trRss);
        log.info("{}", trRssTorrents);
    }

}
