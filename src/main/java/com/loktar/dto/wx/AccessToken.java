package com.loktar.dto.wx;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@NoArgsConstructor
public class AccessToken extends BaseResult implements Serializable {
    private String accessToken;
    private int expiresIn;
}
