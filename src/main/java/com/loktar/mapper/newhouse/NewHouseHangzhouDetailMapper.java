package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouDetail;
import java.util.List;

public interface NewHouseHangzhouDetailMapper {
    int deleteByPrimaryKey(String detailId);

    int insert(NewHouseHangzhouDetail row);

    NewHouseHangzhouDetail selectByPrimaryKey(String detailId);

    List<NewHouseHangzhouDetail> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouDetail row);

    void deleteByHouseIdAndPresellId(String houseId, String presellId);

    int selectCountByHouseId(String houseId);

    int getAvgPrice(String houseId);
}