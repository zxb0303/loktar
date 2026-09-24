package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentDetail implements Serializable {
    private String patentId;

    private String name;

    private String applyId;

    private String applyName;

    private String type;

    private String applyDate;

    private String pubNoticeNum;

    private String authNoticeNum;

    private String legalStatus;

    private String caseStatus;

    private String authNoticeDate;

    private String mainCategoryNum;

    private Integer status;
}