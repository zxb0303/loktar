package com.loktar.dto.transmission;

import lombok.Data;

@Data
public class TrRequest {
    private String method;
    private TrRequestArg arguments;
    private String tag;
}
