package com.loktar.mapper.patent;

import com.loktar.domain.patent.PatentPdf;
import java.util.List;

public interface PatentPdfMapper {
    int deleteByPrimaryKey(String patentId);

    int insert(PatentPdf row);

    PatentPdf selectByPrimaryKey(String patentId);

    List<PatentPdf> selectAll();

    int updateByPrimaryKey(PatentPdf row);

    void insertBatch(List<PatentPdf> patentPdfs);
}