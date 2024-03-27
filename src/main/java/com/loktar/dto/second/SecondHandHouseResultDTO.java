package com.loktar.dto.second;

import lombok.Data;

import java.util.List;

@Data
public class SecondHandHouseResultDTO {
    private int totaltows;
    private String pageinfo;
    private List<SecondHandHouseDTO> list;
}
