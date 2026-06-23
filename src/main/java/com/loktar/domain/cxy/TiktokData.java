package com.loktar.domain.cxy;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TiktokData implements Serializable {
    private Integer dataId;

    private Integer accountId;

    private String date;

    private Integer todayDiamondCnt;

    private Integer todayLiveCnt;

    private Integer todayNewMemberCnt;

    private Integer monthDiamondCnt;

    private String monthDiamondPct;

    private Integer monthNewCreatorCnt;

    private Integer monthNewCreatorDiamondCnt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}