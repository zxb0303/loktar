package com.loktar.mapper.investment;

import com.loktar.domain.investment.EquityIndexDividendYieldDaily;
import java.util.List;

public interface EquityIndexDividendYieldDailyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EquityIndexDividendYieldDaily row);

    EquityIndexDividendYieldDaily selectByPrimaryKey(Integer id);

    List<EquityIndexDividendYieldDaily> selectAll();

    int updateByPrimaryKey(EquityIndexDividendYieldDaily row);

    int insertIgnore(EquityIndexDividendYieldDaily row);

    List<EquityIndexDividendYieldDaily> getRecentEquityIndexDividendYieldDaily();
}