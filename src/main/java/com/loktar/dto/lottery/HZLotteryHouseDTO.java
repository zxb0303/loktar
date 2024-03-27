package com.loktar.dto.lottery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HZLotteryHouseDTO {
    private String id;
    private int rownum;
    @JsonProperty("house_name")
    private String houseName;
    @JsonProperty("lottery_time")
    private String lotteryTime;
    private int status;
    //总报名家庭数1741
    private int total_people;
    //总房源数184
    private int house_number;
    //人才家庭数19
    private int elite_people;
    //人才房源数27
    private int elite_room;
    //无房家庭数503
    private int homeless_people;
    //无房房源数37
    private int homeless_number;
    //有房
    private int home_people;


    private int lottery_number;

}
