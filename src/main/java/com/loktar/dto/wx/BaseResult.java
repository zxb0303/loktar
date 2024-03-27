package com.loktar.dto.wx;

import lombok.Data;


@Data
public class BaseResult {

    public static final int ERRORCODE_SUCCESS = 0;

    private int errcode;
    private String errmsg;

    public boolean isSuccess() {
        if (errcode == ERRORCODE_SUCCESS) {
            return true;
        }
        return false;
    }
}
