package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouV3PresellBuild implements Serializable {
    private String buildId;

    private String buildNo;

    private String presellId;

    private String houseId;

    private static final long serialVersionUID = 1L;
}