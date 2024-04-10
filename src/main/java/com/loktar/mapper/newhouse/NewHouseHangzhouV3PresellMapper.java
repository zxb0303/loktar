package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouV3Presell;
import java.util.List;

public interface NewHouseHangzhouV3PresellMapper {
    int deleteByPrimaryKey(String presellId);

    int insert(NewHouseHangzhouV3Presell row);

    NewHouseHangzhouV3Presell selectByPrimaryKey(String presellId);

    List<NewHouseHangzhouV3Presell> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouV3Presell row);

    void insertBatch(List<NewHouseHangzhouV3Presell> newHouseHangzhouV3Presells);

    List<NewHouseHangzhouV3Presell> selectByHouseId(String houseId);
}