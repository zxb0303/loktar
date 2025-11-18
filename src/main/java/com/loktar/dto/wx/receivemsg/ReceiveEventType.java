package com.loktar.dto.wx.receivemsg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReceiveEventType {
    CLICK("click"),
    VIEW("view");
    private final String name;

    public static ReceiveEventType getByName(String name) {
        if (name == null) {
            return null;
        }
        for (ReceiveEventType type : ReceiveEventType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
