package com.loktar.domain.cxy;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TiktokCreatorData implements Serializable {
    private Integer userId;

    private Integer accountId;

    private String date;

    private Integer rank;

    private String displayId;

    private Integer incomeCurrent;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}