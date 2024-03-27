package com.loktar.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单的点击事件
 *
 * @author zxb
 */
@Getter
@AllArgsConstructor
public enum KeyButtonType {
    // 按钮点击
    CLICK("click"),
    // 链接
    VIEW("view"),
    // 小程序
    MINI_PROGRAM("miniprogram"),
    // 图文模板
    MEDIAID("media_id"),
    // 图文链接
    VIEWLIMITED("view_limited"),
    // 扫码
    SCANCODE_PUSH("scancode_push"),
    // 扫码等待消息
    SCANCODE_WAITMSG("scancode_waitmsg"),
    // 拍照
    PIC_SYSPHOTO("pic_sysphoto"),
    // 拍照或相册发图
    PIC_PHOTO_OR_ALBUM("pic_photo_or_album"),
    // 微信相册发图
    PIC_WEIXIN("pic_weixin"),
    // 选择地理位置
    LOCATION_SELECT("location_select");

    private String name;
    public static KeyButtonType getByName(String name) {
        for (KeyButtonType type : KeyButtonType.values()) {
            if (type.getName().equals(name))
                return type;
        }
        return null;
    }
}
