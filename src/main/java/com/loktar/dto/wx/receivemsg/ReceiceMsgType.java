package com.loktar.dto.wx.receivemsg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReceiceMsgType {
    TEXT("text"),
    VOICE("voice"),
    EVENT("event") ;

    private String name;
    public static ReceiceMsgType getByName(String name) {
        for (ReceiceMsgType type : ReceiceMsgType.values()) {
            if (type.getName().equals(name))
                return type;
        }
        return null;
    }
}
