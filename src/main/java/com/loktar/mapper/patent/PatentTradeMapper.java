package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentTrade;
import java.util.List;

public interface PatentTradeMapper {
    int deleteByPrimaryKey(String patentId);

    int insert(PatentTrade row);

    PatentTrade selectByPrimaryKey(String patentId);

    List<PatentTrade> selectAll();

    int updateByPrimaryKey(PatentTrade row);
}