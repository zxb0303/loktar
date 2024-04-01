package com.loktar.mapper.lottery;

import com.loktar.domain.lottery.LotteryPeople;
import java.util.List;

public interface LotteryPeopleMapper {
    int deleteByPrimaryKey(String peopleId);

    int insert(LotteryPeople row);

    LotteryPeople selectByPrimaryKey(String peopleId);

    List<LotteryPeople> selectAll();

    int updateByPrimaryKey(LotteryPeople row);

    int deleteLotteryPeopleByHouseId(String houseId);

    void insertBatch(List<LotteryPeople> lotteryPeoples);
}