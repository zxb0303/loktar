package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentApply;
import java.util.List;

public interface PatentApplyMapper {
    int deleteByPrimaryKey(String applyId);

    int insert(PatentApply row);

    PatentApply selectByPrimaryKey(String applyId);

    List<PatentApply> selectAll();

    int updateByPrimaryKey(PatentApply row);

    PatentApply selectByApplyName(String applyName);
}