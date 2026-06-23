package com.loktar.domain.patent;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PatentApplyDetail implements Serializable {
    private String applyDetailId;

    private String patentId;

    private String applyId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}