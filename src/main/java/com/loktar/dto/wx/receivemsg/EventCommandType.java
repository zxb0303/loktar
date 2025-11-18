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
    SHOW_BWG_fLOW("查询搬瓦工流量"),
    UDATE_WX_MENU("更新菜单"),
    TRANSMISSION_SPEED_SWTICH("TR限速开关"),
    PATENT_SEARCH_PROCESS("专利查询进度"),
    PATENT_MONITOR_SWITCH("专利查询监控"),
    RELX_MONITOR_SWITCH("Relx监控开关");


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
