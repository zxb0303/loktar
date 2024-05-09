package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentApplyDetail;
import java.util.List;

public interface PatentApplyDetailMapper {
    int deleteByPrimaryKey(String applyDetailId);

    int insert(PatentApplyDetail row);

    PatentApplyDetail selectByPrimaryKey(String applyDetailId);

    List<PatentApplyDetail> selectAll();

    int updateByPrimaryKey(PatentApplyDetail row);

    PatentApplyDetail selectByPatentIdAndApplyId(String patentId, String applyId);
}