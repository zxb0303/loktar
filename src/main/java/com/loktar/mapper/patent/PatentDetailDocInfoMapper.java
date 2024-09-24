package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentDetailDocInfo;
import java.util.List;

public interface PatentDetailDocInfoMapper {
    int deleteByPrimaryKey(String docInfoId);

    int insert(PatentDetailDocInfo row);

    PatentDetailDocInfo selectByPrimaryKey(String docInfoId);

    List<PatentDetailDocInfo> selectAll();

    int updateByPrimaryKey(PatentDetailDocInfo row);

    void insertBatch(List<PatentDetailDocInfo> patentDetailDocInfos);

    void deleteByPatentId(String patentId);
}