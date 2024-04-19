package com.loktar.mapper.cxy;

import com.loktar.domain.cxy.CompanyProperty;
import java.util.List;

public interface CompanyPropertyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CompanyProperty row);

    CompanyProperty selectByPrimaryKey(Integer id);

    List<CompanyProperty> selectAll();

    int updateByPrimaryKey(CompanyProperty row);
}