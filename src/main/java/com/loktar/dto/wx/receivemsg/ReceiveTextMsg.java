package com.loktar.dto.wx.receivemsg;

import lombok.Data;

@Data
public class ReceiveTextMsg extends ReceiveBaseMsg {
    private String content;
}
