package com.loktar.mapper.cxy;

import com.loktar.domain.cxy.TiktokData;
import java.util.List;

public interface TiktokDataMapper {
    int deleteByPrimaryKey(Integer dataId);

    int insert(TiktokData row);

    TiktokData selectByPrimaryKey(Integer dataId);

    List<TiktokData> selectAll();

    int updateByPrimaryKey(TiktokData row);
}