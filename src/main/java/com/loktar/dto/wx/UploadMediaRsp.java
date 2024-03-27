package com.loktar.dto.wx;

import lombok.Data;

@Data
public class UploadMediaRsp extends BaseResult{
    private String type;
    private String mediaId;
    private String createdAt;
}
