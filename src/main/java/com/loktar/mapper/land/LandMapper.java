package com.loktar.mapper.land;

import com.loktar.domain.land.Land;

import java.util.Date;
import java.util.List;

public interface LandMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Land row);

    Land selectByPrimaryKey(Integer id);

    List<Land> selectAll();

    int updateByPrimaryKey(Land row);

    int deleteByDate(Date date);}