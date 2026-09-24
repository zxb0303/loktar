package com.loktar.domain.newhouse;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewHouseHangzhouV3 implements Serializable {
    private String houseId;

    private String tempHouseId;

    private String name;

    private String useful;

    private String address;

    private String company;

    private String phone;

    private static final long serialVersionUID = 1L;
}