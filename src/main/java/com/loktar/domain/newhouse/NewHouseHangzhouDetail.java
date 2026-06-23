package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouDetail implements Serializable {
    private String detailId;

    private String houseId;

    private String presellId;

    private String buildNo;

    private String unitNo;

    private String roomNo;

    private String buildArea;

    private String innerArea;

    private String areaRate;

    private String recordPrice;

    private String fixPrice;

    private String price;

    private String status;

    private static final long serialVersionUID = 1L;
}