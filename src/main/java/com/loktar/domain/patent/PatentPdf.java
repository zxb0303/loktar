package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentPdf implements Serializable {
    private String patentId;

    private String applyName;

    private String type;

    private String authNoticeNum;

    private String authNoticeDate;

    private String mainCategoryNum;

    private Integer status;

    private static final long serialVersionUID = 1L;
}