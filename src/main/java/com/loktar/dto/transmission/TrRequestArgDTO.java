package com.loktar.dto.transmission;

import lombok.Data;

@Data
public class TrRequestArgDTO {
    private String path;
    private String[] fields;
    private Integer[] ids;
}
