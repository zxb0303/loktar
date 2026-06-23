package com.loktar.domain.second;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SecondHandHouse implements Serializable {
    private Integer id;

    private String fwtybh;

    private String xzqhname;

    private String cqmc;

    private String xqmc;

    private Float jzmj;

    private String wtcsjg;

    private String mdmc;

    private String gplxrxm;

    private String scgpshsj;

    private String status;

    private LocalDateTime statusTime;

    private static final long serialVersionUID = 1L;
}