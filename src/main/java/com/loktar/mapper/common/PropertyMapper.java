package com.loktar.mapper.common;

import com.loktar.domain.common.Property;
import java.util.List;

public interface PropertyMapper {
    int deleteByPrimaryKey(String id);

    int insert(Property row);

    Property selectByPrimaryKey(String id);

    List<Property> selectAll();

    int updateByPrimaryKey(Property row);
}