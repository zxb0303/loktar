package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentBaseMsg {
    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private String agentid;
}
