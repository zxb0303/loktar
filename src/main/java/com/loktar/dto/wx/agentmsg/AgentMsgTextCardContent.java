package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgTextCardContent {
    public String title;
    public String description;
    public String url;

    public AgentMsgTextCardContent(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
