package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgMedia {
    private String mediaId;

    public AgentMsgMedia(String mediaId) {
        this.mediaId = mediaId;
    }
}
