package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouPresell implements Serializable {
    private String presellId;

    private String houseId;

    private String presellNo;

    private Double price;

    private String date;

    private Integer totalHouseNum;

    private Integer limitHouseNum;

    private Integer soldHouseNum;

    private Integer updateStatus;

    private static final long serialVersionUID = 1L;
}