package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentContent;
import java.util.List;

public interface PatentContentMapper {
    int deleteByPrimaryKey(String patentId);

    int insert(PatentContent row);

    PatentContent selectByPrimaryKey(String patentId);

    List<PatentContent> selectAll();

    int updateByPrimaryKey(PatentContent row);

    List<PatentContent> selectByStatus(int status);

    void insertBatch(List<PatentContent> patentContents);
}