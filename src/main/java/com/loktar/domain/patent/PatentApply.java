package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentApply implements Serializable {
    private String applyId;

    private String applyName;

    private Integer patentCount;

    private Integer status;

    private static final long serialVersionUID = 1L;
}