package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PatentPdfApply implements Serializable {
    private String applyId;

    private String applyName;

    private Integer patentAuthCount2020;

    private Integer patentAuthCount2021;

    private Integer patentAuthCount2022;

    private Integer patentAuthCount2023;

    private Integer patentAuthCount2024;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}