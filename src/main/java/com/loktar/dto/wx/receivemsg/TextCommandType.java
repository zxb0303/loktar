package com.loktar.dto.wx.receivemsg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TextCommandType {
    ADD_NOTICE("添加通知");

    private String name;

    public static TextCommandType getByName(String name) {
        for (TextCommandType type : TextCommandType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

}
