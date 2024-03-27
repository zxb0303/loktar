package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouV2;
import java.util.List;

public interface NewHouseHangzhouV2Mapper {
    int deleteByPrimaryKey(String houseId);

    int insert(NewHouseHangzhouV2 row);

    NewHouseHangzhouV2 selectByPrimaryKey(String houseId);

    List<NewHouseHangzhouV2> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouV2 row);
}