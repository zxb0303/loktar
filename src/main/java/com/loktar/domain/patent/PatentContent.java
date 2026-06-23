package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PatentContent implements Serializable {
    private String patentId;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String content;

    private static final long serialVersionUID = 1L;
}