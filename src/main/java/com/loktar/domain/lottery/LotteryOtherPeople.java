package com.loktar.domain.lottery;

import lombok.Data;

import java.io.Serializable;

@Data
public class LotteryOtherPeople implements Serializable {
    private String otherPeopleId;

    private String houseId;

    private String peopleId;

    private String name;

    private String identityNum;

    private String recordNum;
}