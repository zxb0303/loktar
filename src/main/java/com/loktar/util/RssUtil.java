package com.loktar.util;



import com.loktar.conf.LokTarConstant;
import com.loktar.domain.transmission.TrRss;
import com.loktar.domain.transmission.TrRssTorrent;
import com.loktar.dto.rss.RssFeed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RssUtil {

    public static final XmlMapper xmlMapper = XmlMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private final HttpClient httpClient;

    public RssUtil(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private final static int RSS_FETCH_MAX_ATTEMPTS = 3;
    // link中ID提取：参数型 xxx.php?id=639203 优先，否则取最后一个数字串（兼容路径型 detail/1275528）
    private final static Pattern PATTERN_LINK_ID_PARAM = Pattern.compile("id=(\\d+)");
    private final static Pattern PATTERN_LINK_ID_LAST_NUM = Pattern.compile("(\\d+)(?!.*\\d)");

    public List<TrRssTorrent> getRssData(TrRss trRss) {
        List<TrRssTorrent> trRssTorrents = new ArrayList<>();
        String rssBody = fetchRssWithRetry(trRss.getRssUrl());
        if (rssBody == null) {
            // 抓取失败返回空列表跳过本轮，已有数据保持不变，下轮调度补偿
            return trRssTorrents;
        }
        RssFeed rssFeed;
        try {
            rssFeed = xmlMapper.readValue(rssBody, RssFeed.class);
        } catch (JacksonException e) {
            // 响应非XML（如站点按Accept内容协商返回JSON）或格式异常时跳过本轮，已有数据保持不变，下轮调度补偿
            log.warn("RSS解析失败，跳过本轮，url：{}，原因：{}", trRss.getRssUrl(), e.getMessage());
            return trRssTorrents;
        }
        rssFeed.getChannel().getItem().forEach(item -> {
            TrRssTorrent trRssTorrent = new TrRssTorrent();
            trRssTorrent.setRssId(trRss.getRssId());
            trRssTorrent.setTitle(item.getTitle());
            trRssTorrent.setDescription(item.getDescription());
            trRssTorrent.setLink(item.getLink());
            if (ObjectUtils.isEmpty(item.getLink())) {
                log.warn("RSS条目link为空，跳过，url：{}", trRss.getRssUrl());
                return;
            }
            Matcher idMatcher = PATTERN_LINK_ID_PARAM.matcher(item.getLink());
            if (!idMatcher.find()) {
                idMatcher = PATTERN_LINK_ID_LAST_NUM.matcher(item.getLink());
                if (!idMatcher.find()) {
                    log.warn("RSS条目link无法提取ID，跳过，url：{}，link：{}", trRss.getRssUrl(), item.getLink());
                    return;
                }
            }
            int id = Integer.parseInt(idMatcher.group(1));
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
     * 抓取 RSS，超时/网络异常时重试（HttpTimeoutException 是 IOException 的子类）；重试耗尽返回null由调用方跳过本轮
     */
    private String fetchRssWithRetry(String rssUrl) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(rssUrl))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_XML)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .GET()
                .build();
        for (int attempt = 1; attempt <= RSS_FETCH_MAX_ATTEMPTS; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException e) {
                log.warn("RSS抓取失败，第 {}/{} 次尝试：{}，url：{}", attempt, RSS_FETCH_MAX_ATTEMPTS, e.getMessage(), rssUrl);
                if (attempt < RSS_FETCH_MAX_ATTEMPTS) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        log.warn("RSS抓取连续{}次失败，跳过本轮，url：{}", RSS_FETCH_MAX_ATTEMPTS, rssUrl);
        return null;
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
