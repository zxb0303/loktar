package com.loktar.dto.wx;

import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
public class AccessToken extends BaseResult {
    private String accessToken;
    private int expiresIn;
}
