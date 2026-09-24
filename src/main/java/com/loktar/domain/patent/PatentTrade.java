package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentTrade implements Serializable {
    private String patentId;

    private String fromApplyName;

    private String toApplyName;

    private static final long serialVersionUID = 1L;
}