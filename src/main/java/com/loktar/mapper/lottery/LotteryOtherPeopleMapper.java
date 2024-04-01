package com.loktar.mapper.lottery;

import com.loktar.domain.lottery.LotteryOtherPeople;
import java.util.List;

public interface LotteryOtherPeopleMapper {
    int deleteByPrimaryKey(String otherPeopleId);

    int insert(LotteryOtherPeople row);

    LotteryOtherPeople selectByPrimaryKey(String otherPeopleId);

    List<LotteryOtherPeople> selectAll();

    int updateByPrimaryKey(LotteryOtherPeople row);

    int deleteLotteryOtherPeoplesByHouseId(String houseId);

    void insertBatch(List<LotteryOtherPeople> lotteryOtherPeoples);
}