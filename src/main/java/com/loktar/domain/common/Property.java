package com.loktar.domain.common;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Property implements Serializable {
    private String id;

    private String type;

    private String value;

    private String value2;

    private String value3;

    private String status;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}