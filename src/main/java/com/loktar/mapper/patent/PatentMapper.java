package com.loktar.mapper.patent;

import com.loktar.domain.patent.Patent;

import java.util.List;

public interface PatentMapper {
    int deleteByPrimaryKey(String patentId);

    int insert(Patent row);

    Patent selectByPrimaryKey(String patentId);

    List<Patent> selectAll();

    int updateByPrimaryKey(Patent row);

    void insertBatch(List<Patent> patents);

    List<Patent> selectByStatus(int status);

}