package com.loktar.mapper.patent;

import com.loktar.dto.patent.PatentContractDTO;

public interface CompanyInfoCqqMapper {
    PatentContractDTO getPatentContractDTOByApplyName(String applyName);
}
