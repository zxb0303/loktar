package com.loktar.domain.lottery;

import lombok.Data;

import java.io.Serializable;

@Data
public class LotteryHouse implements Serializable {
    private String houseId;

    private String houseName;

    private Integer totalPeopleNum;

    private Integer totalHouseNum;

    private Integer elitePeopleNum;

    private Integer eliteHouseNum;

    private String eliteChance;

    private Integer homelessPeopleNum;

    private Integer homelessHouseNum;

    private String homelessChance;

    private Integer unhomelessPeopleNum;

    private Integer unhomelessHouseNum;

    private String unhomelessChance;

    private Integer status;

    private String lotteryTime;
}