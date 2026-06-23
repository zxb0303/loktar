package com.loktar.domain.lottery;

import lombok.Data;

import java.io.Serializable;

@Data
public class LotteryPeople implements Serializable {
    private String peopleId;

    private String serialNum;

    private String houseId;

    private String name;

    private String identityNum;

    private Integer familyType;

    private Integer hasOtherPeople;

    private Integer lotteryRank;

    private String recordNum;

    private static final long serialVersionUID = 1L;
}