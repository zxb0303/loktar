package com.loktar.domain.cxy;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private Integer id;

    private String name;

    private String startDate;

    private String endDate;

    private Integer status;

    private static final long serialVersionUID = 1L;
}