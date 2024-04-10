package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouV3;
import java.util.List;

public interface NewHouseHangzhouV3Mapper {
    int deleteByPrimaryKey(String houseId);

    int insert(NewHouseHangzhouV3 row);

    NewHouseHangzhouV3 selectByPrimaryKey(String houseId);

    List<NewHouseHangzhouV3> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouV3 row);

    NewHouseHangzhouV3 selectByName(String name);
}