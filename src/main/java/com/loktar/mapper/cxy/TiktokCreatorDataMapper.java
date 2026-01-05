package com.loktar.mapper.cxy;

import com.loktar.domain.cxy.TiktokCreatorData;
import java.util.List;

public interface TiktokCreatorDataMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(TiktokCreatorData row);

    TiktokCreatorData selectByPrimaryKey(Integer userId);

    List<TiktokCreatorData> selectAll();

    int updateByPrimaryKey(TiktokCreatorData row);
}