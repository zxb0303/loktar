package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentDetailYitong;
import java.util.List;

public interface PatentDetailYitongMapper {
    int deleteByPrimaryKey(String patentId);

    int insert(PatentDetailYitong row);

    PatentDetailYitong selectByPrimaryKey(String patentId);

    List<PatentDetailYitong> selectAll();

    int updateByPrimaryKey(PatentDetailYitong row);
}