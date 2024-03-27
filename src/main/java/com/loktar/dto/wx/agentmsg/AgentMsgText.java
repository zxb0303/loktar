package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgText extends AgentBaseMsg{
    private AgentMsgTextContent text;

    public AgentMsgText(String toUser,String agentId,String text) {
        this.setTouser(toUser);
        this.setAgentid(agentId);
        this.setMsgtype(AgentMsgType.TEXT.getName());
        this.text = new AgentMsgTextContent(text);
    }
}
