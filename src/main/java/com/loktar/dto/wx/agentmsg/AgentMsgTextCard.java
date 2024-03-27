package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgTextCard extends AgentBaseMsg{
    private AgentMsgTextCardContent textcard;

    public AgentMsgTextCard(String toUser, String agentId, String title,String content,String url) {
        this.setTouser(toUser);
        this.setAgentid(agentId);
        this.setMsgtype(AgentMsgType.TEXT_CARD.getName());

        this.textcard = new AgentMsgTextCardContent(title,content,url);
    }

    public AgentMsgTextCard(String toUser, String agentId, AgentMsgTextCardContent agentMsgTextCardContent) {
        this.setTouser(toUser);
        this.setAgentid(agentId);
        this.setMsgtype(AgentMsgType.TEXT_CARD.getName());
        this.textcard = agentMsgTextCardContent;
    }

}
