package com.loktar.dto.wx.receivemsg;

import lombok.Data;

@Data
public class ReceiveBaseMsg {
    private String toUserName;
    private String fromUserName;
    private String createTime;
    private String msgType;
    private String msgId;
    private String event;
    private String agentID;
}
