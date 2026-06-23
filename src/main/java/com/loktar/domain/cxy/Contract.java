package com.loktar.domain.cxy;

import lombok.Data;

import java.io.Serializable;

@Data
public class Contract implements Serializable {
    private Integer id;

    private String party;

    private String type;

    private String number;

    private String counterParty;

    private String startDate;

    private String endDate;

    private Integer status;

    private static final long serialVersionUID = 1L;
}