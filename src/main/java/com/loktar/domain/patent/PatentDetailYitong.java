package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PatentDetailYitong implements Serializable {
    private String patentId;

    private String type;

    private String user;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}