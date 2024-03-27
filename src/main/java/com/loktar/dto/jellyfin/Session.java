package com.loktar.dto.jellyfin;

import lombok.Data;

@Data
public class Session {
    private String userId;
    private String userName;
    private String client;
    private String deviceName;
    //ip
    private String remoteEndPoint;
}
