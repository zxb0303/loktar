package com.loktar.domain.cxy;

import lombok.Data;

import java.io.Serializable;

@Data
public class TiktokAccount implements Serializable {
    private Integer accountId;

    private String country;

    private String email;

    private String password;

    private String factionId;

    private Integer status;

    private String excelStart;

    private String excelEnd;

    private String excelMonthlyIndex;

    private String excelMonthlyDataIndex;

    private String excelMonthlyDataPartner;

    private static final long serialVersionUID = 1L;
}