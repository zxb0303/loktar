package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NewHouseHangzhouV3PresellBuild implements Serializable {
    private String buildId;

    private String buildNo;

    private String presellId;

    private String houseId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}