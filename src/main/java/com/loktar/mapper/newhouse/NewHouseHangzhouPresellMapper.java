package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouPresell;
import java.util.List;

public interface NewHouseHangzhouPresellMapper {
    int deleteByPrimaryKey(String presellId);

    int insert(NewHouseHangzhouPresell row);

    NewHouseHangzhouPresell selectByPrimaryKey(String presellId);

    List<NewHouseHangzhouPresell> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouPresell row);

    List<NewHouseHangzhouPresell> getPresellByHouseIdAndStatus(String houseId, int i);

    int getLimitSumByHouseId(String houseId);
}