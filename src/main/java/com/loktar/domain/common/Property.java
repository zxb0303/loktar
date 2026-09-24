package com.loktar.domain.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Property implements Serializable {
    private String id;

    private String type;

    private String value;

    private String value2;

    private String value3;

    private String value4;

    private String status;

    private static final long serialVersionUID = 1L;
}