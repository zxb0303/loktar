package com.loktar.domain.investment;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EquityIndexPerfDaily implements Serializable {
    private Long id;

    private String indexCode;

    private String indexName;

    private LocalDate tradeDate;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal change;

    private BigDecimal changePct;

    private Double tradingVol;

    private BigDecimal tradingValue;

    private Integer consNumber;

    private BigDecimal peg;

    private static final long serialVersionUID = 1L;
}