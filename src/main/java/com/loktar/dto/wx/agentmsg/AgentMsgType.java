package com.loktar.dto.wx.agentmsg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentMsgType {
    TEXT("text"),
    IMAGE("image"),
    VOICE("voice"),
    VIDEO("video"),
    FILE("file"),
    TEXT_CARD("textcard"),
    NEWS("news"),
    MARKDOWN("markdown");

    private final String name;
    public static AgentMsgType getByName(String name) {
        for (AgentMsgType type : AgentMsgType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
