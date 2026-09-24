package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouV3Detail implements Serializable {
    private String detailId;

    private String buildId;

    private String presellId;

    private String houseId;

    private String buildNo;

    private String unitNo;

    private String roomNo;

    private String buildArea;

    private String innerArea;

    private String areaRate;

    private String unitPrice;

    private String totalPrice;

    private Integer status;

    private static final long serialVersionUID = 1L;
}