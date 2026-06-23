package com.loktar.domain.investment;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FundNav implements Serializable {
    private Long id;

    private String fundCode;

    private LocalDate navDate;

    private BigDecimal unitNav;

    private BigDecimal accNav;

    private BigDecimal growthRate;

    private String subscribeStatus;

    private String redeemStatus;

    private BigDecimal bonus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}