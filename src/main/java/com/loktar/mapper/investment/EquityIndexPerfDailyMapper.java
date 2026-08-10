package com.loktar.mapper.investment;

import com.loktar.domain.investment.EquityIndexPerfDaily;
import java.time.LocalDate;
import java.util.List;

public interface EquityIndexPerfDailyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EquityIndexPerfDaily row);

    EquityIndexPerfDaily selectByPrimaryKey(Long id);

    List<EquityIndexPerfDaily> selectAll();

    int updateByPrimaryKey(EquityIndexPerfDaily row);

    EquityIndexPerfDaily selectByIndexCodeAndTradeDate(String indexCode, LocalDate tradeDate);

    boolean existsByIndexCodeAndTradeDate(String indexCode, LocalDate tradeDate);
}