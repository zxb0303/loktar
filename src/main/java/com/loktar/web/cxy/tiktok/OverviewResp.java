package com.loktar.web.cxy.tiktok;
import lombok.Data;
import java.util.List;
@Data
public class OverviewResp {
    private AnchorCnt anchorCnt;
    private int anchorNum;
    private BaseResp baseResp;
    private String groupName;

    private List<MetricsData> metricsData;

    private List<Long> thisMonthInterval;
    private List<TodayMetricsData> thisMonthMetricsData;

    private List<Long> thisWeekInterval;
    private List<TodayMetricsData> thisWeekMetricsData;

    private long updateTime;

    @Data
    public static class AnchorCnt {
        private String indicatorName;
        private String value;
    }

    @Data
    public static class BaseResp {
        private int statusCode;
        private String statusMessage;
        private String prompts;
    }

    /**
     * 你的 JSON 里 MetricsData: []，没给元素结构。
     * 先占位；如果后续有字段，再把这里补全。
     */
    @Data
    public static class MetricsData {
    }

    @Data
    public static class TodayMetricsData {
        private String indicatorName;
        private String samePeriodPercentageIncrease;
        private String samePeriodValue;
        private String value;
    }
}
