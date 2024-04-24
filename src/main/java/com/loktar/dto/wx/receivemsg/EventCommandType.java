package com.loktar.dto.wx.receivemsg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventCommandType {
    SHOW_COMMAND("查看命令"),
    SHOW_NOTICE("查看通知"),
    SHOW_DOWNLOAD_LIST("查看下载列表"),
    SHOW_CLASH_RSS("查看Clash订阅地址"),
    SHOW_TRANSMISSION_ALT_SPEED("查看TR限速状态"),
    SHOW_BWG_fLOW("搬瓦工流量查询"),
    UDATE_WX_MENU("更新菜单"),
    ALT_TRANSMISSION_SPEED("TR限速开关");

    private final String name;

    public static EventCommandType getByName(String name) {
        for (EventCommandType type : EventCommandType.values()) {
            if (type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

}
