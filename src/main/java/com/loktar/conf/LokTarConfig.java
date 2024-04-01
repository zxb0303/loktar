package com.loktar.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class LokTarConfig {
    @Value("${spring.profiles.active}")
    public String env;

    @Value("${conf.transmission.url}")
    public String transmissionUrl;

    @Value("${conf.transmission.authorization}")
    public String transmissionAuthorization;

    @Value("${conf.transmission.minSizeGB}")
    public long transmissionMinSizeGB;

    @Value("${conf.transmission.days}")
    public int transmissionDays;

    @Value("${conf.transmission.tempDownloadDir}")
    public String transmissionTempDownloadDir;

    @Value("${conf.github.authorization}")
    public String githubAuthorization;

    @Value("${conf.azure.voiceKey}")
    public String azureVoiceKey;

    @Value("${conf.bwg.apiKey}")
    public String bwgApiKey;

    @Value("${conf.openai.apiKey}")
    public String openaiApiKey;

    @Value("${conf.jellyfin.url}")
    public String jellyfinUrl;

    @Value("${conf.jellyfin.token}")
    public String jellyfinToken;

    @Value("${conf.docker.tcpUrl}")
    public String dockerTcpUrl;

    @Value("${conf.docker.apiVersion}")
    public String dockerTcpApiVersion;

    @Value("${conf.qywx.corpid}")
    public String qywxCorpId;

    @Value("${conf.qywx.token}")
    public String qywxToken;

    @Value("${conf.qywx.encodingAeskey}")
    public String qywxEncodingAESKey;

    @Value("${conf.qywx.noticeZxb}")
    public String qywxNoticeZxb;

    @Value("${conf.qywx.noticeCxy}")
    public String qywxNoticeCxy;

    @Value("${conf.qywx.agent002Id}")
    public String qywxAgent002Id;

    @Value("${conf.qywx.agent002Secert}")
    public String qywxAgent002Secert;

    @Value("${conf.qywx.agent003Id}")
    public String qywxAgent003Id;

    @Value("${conf.qywx.agent003Secert}")
    public String qywxAgent003Secert;

    @Value("${conf.qywx.agent004Id}")
    public String qywxAgent004Id;

    @Value("${conf.qywx.agent004Secert}")
    public String qywxAgent004Secert;

    @Value("${conf.common.cxyNoticeText}")
    public String commonCxyNoticeText;

    @Value("${conf.common.clashRssUrl}")
    public String commonClashRssUrl;




}
