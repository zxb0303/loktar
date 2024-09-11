package com.loktar.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "conf")
@Data
public class LokTarConfig {
    
    private Transmission transmission;
    private Jellyfin jellyfin;
    private Docker docker;
    private Qywx qywx;
    private Github github;
    private Azure azure;
    private Bwg bwg;
    private Openai openai;
    private Path path;
    private Common common;


    @Data
    public static class Transmission {
        private String url;
        private String authorization;
        private long minSizeGB;
        private int days;
        private String tempDownloadDir;
    }

    @Data
    public static class Jellyfin {
        private String url;
        private String token;
    }

    @Data
    public static class Docker {
        private String tcpUrl;
        private String apiVersion;
    }

    @Data
    public static class Qywx {
        private String corpid;
        private String token;
        private String encodingAeskey;
        private String noticeZxb;
        private String noticeCxy;
        private String agent002Id;
        private String agent002Secert;
        private String agent003Id;
        private String agent003Secert;
        private String agent004Id;
        private String agent004Secert;
        private String agent006Id;
        private String agent006Secert;
        private String agent007Id;
        private String agent007Secert;
    }

    @Data
    public static class Github {
        private String authorization;
    }

    @Data
    public static class Azure {
        private String voiceKey;
        private String voiceRegion;
        private String docIntelligenceKey;
        private String docIntelligenceEndpoint;
    }

    @Data
    public static class Bwg {
        private String apiKey;
    }

    @Data
    public static class Openai {
        private String apiKey;
    }

    @Data
    public static class Path {
        private String pdf;
        private String voice;
        private String newhouseOriginal;
        private String newhouseCover;
        private String patent;
    }

    @Data
    public static class Common {
        private String loktarUrl;
        private String cxyNoticeText;
        private String clashRssUrl;
    }
}
