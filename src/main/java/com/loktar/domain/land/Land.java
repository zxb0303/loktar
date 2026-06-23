package com.loktar.domain.land;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Land implements Serializable {
    private Integer id;

    private LocalDate date;

    private String city;

    private String area;

    private String landNo;

    private String landName;

    private String status;

    private Float acreage;

    private String landUsage;

    private String volumetricRate;

    private Float dealPrice;

    private Float buildPrice;

    private String premiumRate;

    private String owner;

    private String remark;

    private String detailUrl;

    private static final long serialVersionUID = 1L;
}