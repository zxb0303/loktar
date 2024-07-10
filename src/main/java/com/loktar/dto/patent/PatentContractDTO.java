package com.loktar.dto.patent;

import com.loktar.domain.patent.PatentDetail;
import lombok.Data;

import java.util.List;

@Data
public class PatentContractDTO {
    private String applyName;
    private String applyId;
    private String companyNo;
    private String legalPerson;
    private String price;
    private String priceChinese;
    private String date;
    private List<PatentDetail> patentDetails;
}
