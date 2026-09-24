package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatentDetailYitong implements Serializable {
    private String patentId;

    private String type;

    private String user;

    private static final long serialVersionUID = 1L;
}