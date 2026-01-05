package com.loktar.web.cxy.tiktok;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnchorDataResp {
    @JsonProperty("status_code")
    private Integer statusCode;

    private String message;

    private DataWrapper data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataWrapper {
        // 对应 JSON 中的 "anchorAnalysisDataInfos"
        private List<AnchorInfo> anchorAnalysisDataInfos;

        private BigDecimal dailyTotalAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnchorInfo {
        @JsonProperty("anchorID")
        private String anchorId;

        private String groupID;
        private Long joinedTime;

        // 基础信息
        private AnchorBaseInfo anchorBaseInfo;

        // --- 核心指标 (每个指标都有 current, growth, last 等字段) ---

        // 收入 (income)
        private MetricData income;

        // 直播时长 (liveDuration)
        private MetricData liveDuration;

        // 开播次数 (liveCount)
        private MetricData liveCount;

        // 看播人数 (viewers)
        private MetricData viewers;

        // 礼物数 (gifts)
        private MetricData gifts;

        // 送礼人数 (gifters)
        private MetricData gifters;

        // 平均在线 (avgACU)
        private MetricData avgACU;

        // 最高在线 (avgPCU)
        private MetricData avgPCU;

        // 曝光 (impression)
        private MetricData impression;

        // 海外钻石 (overseaDiamond)
        private MetricData overseaDiamond;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnchorBaseInfo {
        private Long anchorCreateTime;

        @JsonProperty("user_base_info")
        private UserBaseInfo userBaseInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserBaseInfo {
        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("display_id")
        private String displayId;

        private String nickname;
        private String avatar;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetricData {
        // 使用 BigDecimal 因为有些是整数(income)，有些是浮点数(overseaDiamondPercentage)
        private int current;
        private BigDecimal last;

        // growth 在 JSON 中是字符串 "-13.51351"
        private String growth;
    }
}
