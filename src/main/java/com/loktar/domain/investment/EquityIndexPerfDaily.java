package com.loktar.domain.investment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}