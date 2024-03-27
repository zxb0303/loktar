package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgTextContent {
    public String content;

    public AgentMsgTextContent(String content) {
        this.content = content;
    }
}
