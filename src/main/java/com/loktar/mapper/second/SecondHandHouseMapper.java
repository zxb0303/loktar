package com.loktar.mapper.second;

import com.loktar.domain.second.SecondHandHouse;

import java.util.List;

public interface SecondHandHouseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SecondHandHouse row);

    SecondHandHouse selectByPrimaryKey(Integer id);

    List<SecondHandHouse> selectAll();

    int updateByPrimaryKey(SecondHandHouse row);

    String getMaxDate();

    List<SecondHandHouse> getNeedUpdateStatus();

    int updateStatusAndStatusTimeByFwtybh(String fwtybh ,String status);

    int insertBatch(List<SecondHandHouse> secondHandHouses);

}