package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentApplyDetail implements Serializable {
    private String applyDetailId;

    private String patentId;

    private String applyId;

    private static final long serialVersionUID = 1L;
}