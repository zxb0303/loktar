package com.loktar.dto.wx.agentmsg;

import com.loktar.dto.wx.BaseResult;
import lombok.Data;

@Data
public class AgentMsgRsp extends BaseResult{
    private String invaliduser;
    private String invalidparty;
    private String invalidtag;
    private String unlicenseduser;
    private String msgid;


}
