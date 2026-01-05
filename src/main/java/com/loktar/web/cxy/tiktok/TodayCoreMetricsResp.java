package com.loktar.web.cxy.tiktok;

import lombok.Data;

import java.util.List;

@Data
public class TodayCoreMetricsResp {
    private BaseResp baseResp;
    private List<Long> todayInterval;
    private List<TodayMetricsData> todayMetricsData;
    private long updateTime;

    @Data
    public static class BaseResp {
        private int statusCode;
        private String statusMessage;
        private String prompts;
    }

    @Data
    public static class TodayMetricsData {
        private String indicatorName;
        private String samePeriodPercentageIncrease;
        private String samePeriodValue;
        private String value;
    }
}
