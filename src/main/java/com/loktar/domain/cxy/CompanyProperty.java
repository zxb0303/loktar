package com.loktar.domain.cxy;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyProperty implements Serializable {
    private Integer id;

    private String zhuti;

    private String zichanbianhao;

    private String shebeimingcheng;

    private String pinpai;

    private String xinghao;

    private String shuliang;

    private String danjia;

    private String jine;

    private static final long serialVersionUID = 1L;
}