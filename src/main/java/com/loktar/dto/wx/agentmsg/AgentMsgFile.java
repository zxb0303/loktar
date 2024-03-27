package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgFile extends AgentBaseMsg{
    private AgentMsgMedia file;

    public AgentMsgFile(String toUser, String agentId, String mediaId) {
        this.setTouser(toUser);
        this.setAgentid(agentId);
        this.setMsgtype(AgentMsgType.FILE.getName());
        this.file = new AgentMsgMedia(mediaId);
    }
}
