package com.loktar.web.cxy.tiktok;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NewCreatorOverviewResp {

    private DataNode data;
    private String message;
    private String prompts;

    private int statusCode;

    @Data
    public static class DataNode {
        private int imCreator;
        private double imCreatorLiveRate;
        private double imCreatorQuitRate;
        private int imQuitCreator;

        private int invitationCodeCreator;
        private double invitationCodeLiveRate;
        private int invitationCodeQuitCreator;
        private double invitationCodeQuitRate;

        private long lastEnd;
        private long lastStart;

        private int liveCreator;
        private double liveCreatorLiveRate;
        private int liveQuitCreator;
        private double liveQuitRate;

        private Map<String, List<DiamondBySourceItem>> newCreatorDiamondBySources;

        private MetricTriple newCreatorDiamonds;

        private MetricTripleDouble newCreatorValidGoLiveRate;

        private MetricSingleDouble newRookieGraduationRate;

        private int othersCreator;
        private double othersLiveRate;
        private int othersQuitCreator;
        private double othersQuitRate;

        private int qrCodeCreator;
        private double qrCodeCreatorLiveRate;
        private int qrCodeQuitCreator;
        private double qrCodeQuitRate;

        private Map<String, Integer> quitReasonMap;

        private int referralCreator;
        private double referralLiveRate;
        private int referralQuitCreator;
        private double referralQuitRate;

        private int shortVideoCreator;
        private double shortVideoCreatorLiveRate;
        private int shortVideoQuitCreator;
        private double shortVideoQuitRate;

        private MetricTriple t15VoluntaryQuitCreator;
        private MetricTripleDouble t15VoluntaryQuitRate;

        private MetricSingle totalNewRookieGraduationCreator;
        private MetricTriple totalValidNewCreator;
    }

    @Data
    public static class DiamondBySourceItem {
        private String date;
        private int diamonds;
        private int source;
    }

    @Data
    public static class MetricTriple {
        private int current;
        private String growth;
        private int last;
    }

    @Data
    public static class MetricTripleDouble {
        private double current;
        private String growth;
        private double last;
    }

    @Data
    public static class MetricSingle {
        private int current;
    }

    @Data
    public static class MetricSingleDouble {
        private double current;
    }
}