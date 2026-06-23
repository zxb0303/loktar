package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PatentDetailDocInfo implements Serializable {
    private String docInfoId;

    private String patentId;

    private String docName;

    private String docDate;

    private String recipient;

    private String postalCode;

    private String downloadDate;

    private String downloadIp;

    private String docType;

    private Integer index;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}