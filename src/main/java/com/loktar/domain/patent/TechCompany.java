package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class TechCompany implements Serializable {
    private String companyId;

    private String name;

    private Integer year;

    private Integer index;

    private static final long serialVersionUID = 1L;
}