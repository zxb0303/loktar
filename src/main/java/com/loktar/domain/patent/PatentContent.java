package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentContent implements Serializable {
    private String patentId;

    private Integer status;

    private String content;

    private static final long serialVersionUID = 1L;
}