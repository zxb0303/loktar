package com.loktar.dto.transmission;

import lombok.Data;

@Data
public class TrRequestDTO {
    private String method;
    private TrRequestArgDTO arguments;
    private String tag;
}
