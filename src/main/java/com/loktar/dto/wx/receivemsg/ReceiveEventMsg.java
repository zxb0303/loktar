package com.loktar.dto.wx.receivemsg;

import lombok.Data;

@Data
public class ReceiveEventMsg extends ReceiveBaseMsg{
    private String event;
    private String eventKey;
}
