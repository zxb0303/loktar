package com.loktar.mapper.lottery;

import com.loktar.domain.lottery.LotteryHouse;
import java.util.List;

public interface LotteryHouseMapper {
    int deleteByPrimaryKey(String houseId);

    int insert(LotteryHouse row);

    LotteryHouse selectByPrimaryKey(String houseId);

    List<LotteryHouse> selectAll();

    int updateByPrimaryKey(LotteryHouse row);

    List<LotteryHouse> selectLotteryedHZHousesByDay(int day);

    int getMaxRankByHouseId(String houseId);

    List<LotteryHouse> selectLotteryedYHHousesByDay(int day);

    List<LotteryHouse> getYesterdayLotteryHouses();
}