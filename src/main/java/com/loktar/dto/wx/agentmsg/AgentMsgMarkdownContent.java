package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgMarkdownContent{
    private String content;
    public AgentMsgMarkdownContent(String content) {
        this.content = content;
    }
}
