package com.loktar.dto.land;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LandDTO {
    @JsonProperty("build_price")
    private String buildPrice;
    private String cityName;
    private String id;
    private String far;
    private String name;
    private String newMemo;
    private String num;
    private String owner;
    private String payPrice;
    @JsonProperty("paystatus_id")
    private String paystatusId;
    private String planName;
    @JsonProperty("premium_ratio")
    private String premiumRatio;
    @JsonProperty("t_area")
    private String tArea;
    private String urbanName;
    @JsonProperty("yu_endTime")
    private String yuEndTime;
}
