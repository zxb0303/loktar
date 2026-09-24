package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouV3Presell implements Serializable {
    private String presellId;

    private String houseId;

    private Integer status;

    private static final long serialVersionUID = 1L;
}