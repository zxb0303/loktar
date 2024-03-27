package com.loktar.dto.wx.receivemsg;

import lombok.Data;

@Data
public class ReceiveVoiceMsg extends ReceiveBaseMsg {
    private String format;
    private String MediaId;
}
