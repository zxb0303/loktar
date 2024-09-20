package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentDetail;

import java.util.List;

public interface PatentDetailMapper {
    int deleteByPrimaryKey(String patentId);

    int insert(PatentDetail row);

    PatentDetail selectByPrimaryKey(String patentId);

    List<PatentDetail> selectAll();

    int updateByPrimaryKey(PatentDetail row);

    List<PatentDetail> selectByStatus(int status);

    void deleteByApplyId(String applyId);

    void insertBatch(List<PatentDetail> patentDetails);

    List<PatentDetail> selectByApplyId(String applyId);

    List<PatentDetail> selectForQuote(String applyId,String type);
}