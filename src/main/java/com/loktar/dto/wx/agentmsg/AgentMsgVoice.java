package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgVoice extends AgentBaseMsg {
    private AgentMsgMedia voice;

    public AgentMsgVoice(String toUser, String agentId, String mediaId) {
        this.setTouser(toUser);
        this.setAgentid(agentId);
        this.setMsgtype(AgentMsgType.VOICE.getName());
        this.voice = new AgentMsgMedia(mediaId);
    }
}
