package com.loktar.domain.investment;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class EquityIndexDividendYieldDaily implements Serializable {
    private Integer id;

    private String equityIndex;

    private String equityIndexName;

    private String date;

    private Float dividendYield;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}