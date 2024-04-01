package com.loktar.dto.transmission;

import lombok.Data;

@Data
public class TrResponse {
    public static String RESULT_SUCCESS = "success";
    private String result;
    private TrResponseArg arguments ;
    private String tag;
}
