package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouV2 implements Serializable {
    private String houseId;

    private String name;

    private String nameSpread;

    private Integer price;

    private String type;

    private Double plotRatio;

    private String greenRatio;

    private String coverArea;

    private String bulidArea;

    private String bulidType;

    private Integer totalHouseNum;

    private Integer carParkNum;

    private String area;

    private String areaCode;

    private String plate;

    private String address;

    private static final long serialVersionUID = 1L;
}