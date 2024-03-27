package com.loktar.service.lottery;


import com.loktar.domain.lottery.LotteryHouse;

import java.util.List;

public interface HZLotteryServiceV2 {
    void updateHZLotteryData();
    void updateLotteryPeoples(List<LotteryHouse> lotteryHouses);
}
