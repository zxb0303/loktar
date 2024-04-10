package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouV3Detail;
import java.util.List;

public interface NewHouseHangzhouV3DetailMapper {
    int deleteByPrimaryKey(String detailId);

    int insert(NewHouseHangzhouV3Detail row);

    NewHouseHangzhouV3Detail selectByPrimaryKey(String detailId);

    List<NewHouseHangzhouV3Detail> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouV3Detail row);
}