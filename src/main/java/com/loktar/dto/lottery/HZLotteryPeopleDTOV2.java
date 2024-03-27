package com.loktar.dto.lottery;

import lombok.Data;

@Data
public class HZLotteryPeopleDTOV2 {
    private String peopleId;
    private String serialNum;
    private String houseId;
    private String name;
    private String identityNum;
    private Integer familyType;
    private Integer hasOtherPeople;
    private Integer lotteryRank;
    private String recordNum;
    private String otherBuyersIdnumber;
    private String otherBuyersName;
}
