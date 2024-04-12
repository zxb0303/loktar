package com.loktar.util;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.transmission.TrRss;
import com.loktar.domain.transmission.TrRssTorrent;
import com.loktar.dto.rss.RssFeed;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RssUtil {

    public final static ObjectMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    @SneakyThrows
    public static List<TrRssTorrent> getRssData(TrRss trRss) {
        List<TrRssTorrent> trRssTorrents = new ArrayList<TrRssTorrent>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(trRss.getRssUrl()))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        RssFeed rssFeed  = xmlMapper.readValue(response.body(), RssFeed.class);
        rssFeed.getChannel().getItem().forEach(item -> {
            TrRssTorrent trRssTorrent = new TrRssTorrent();
            trRssTorrent.setRssId(trRss.getRssId());
            trRssTorrent.setTitle(item.getTitle());
            trRssTorrent.setLink(item.getLink());
            String idStr = item.getLink().split("id=")[1];
            if (idStr.contains("&")) {
                idStr = idStr.split("&")[0];
            }
            int id = Integer.valueOf(idStr);
            trRssTorrent.setRssTorrentId(id);
            if(!ObjectUtils.isEmpty(item.getEnclosure())){
                trRssTorrent.setDownloadUrl(item.getEnclosure().getUrl());
            }else{
                trRssTorrent.setDownloadUrl(item.getLink());
            }
            String dateStr = item.getPubDate();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
            try {
                trRssTorrent.setPubDate(sdf.parse(dateStr));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            trRssTorrent.setStatus(0);
            trRssTorrents.add(trRssTorrent);
        });

        return trRssTorrents;
    }

    public static void main(String[] args)  {
        TrRss trRss = new TrRss();
        trRss.setRssId(111);
        trRss.setRssUrl("");
        List<TrRssTorrent> trRssTorrents = getRssData(trRss);
        System.out.println(trRssTorrents.toString());
    }

}
