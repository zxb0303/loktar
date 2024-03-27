package com.loktar.dto.wx.agentmsg;

import lombok.Data;

@Data
public class AgentMsgMarkdown extends AgentBaseMsg{
    private AgentMsgMarkdownContent markdown;

    public AgentMsgMarkdown(String toUser,String agentId,String markdown) {
        this.setTouser(toUser);
        this.setAgentid(agentId);
        this.setMsgtype(AgentMsgType.MARKDOWN.getName());
        this.markdown = new AgentMsgMarkdownContent(markdown);
    }
}
