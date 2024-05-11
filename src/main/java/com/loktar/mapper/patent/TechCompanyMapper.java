package com.loktar.mapper.patent;

import com.loktar.domain.patent.TechCompany;
import java.util.List;

public interface TechCompanyMapper {
    int deleteByPrimaryKey(String companyId);

    int insert(TechCompany row);

    TechCompany selectByPrimaryKey(String companyId);

    List<TechCompany> selectAll();

    int updateByPrimaryKey(TechCompany row);

    void insertBatch(List<TechCompany> techCompanys);
}