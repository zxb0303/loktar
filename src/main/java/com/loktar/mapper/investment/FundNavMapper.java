package com.loktar.mapper.investment;

import com.loktar.domain.investment.FundNav;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface FundNavMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FundNav row);

    FundNav selectByPrimaryKey(Long id);

    List<FundNav> selectAll();

    int updateByPrimaryKey(FundNav row);

    FundNav selectByFundCodeAndNavDate(@Param("fundCode") String fundCode, @Param("navDate") LocalDate navDate);
}