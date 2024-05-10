package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentPdfApply;
import java.util.List;

public interface PatentPdfApplyMapper {
    int deleteByPrimaryKey(String applyId);

    int insert(PatentPdfApply row);

    PatentPdfApply selectByPrimaryKey(String applyId);

    List<PatentPdfApply> selectAll();

    int updateByPrimaryKey(PatentPdfApply row);

    List<PatentPdfApply> selectByStatusAndLimit(int status,int limit);
}