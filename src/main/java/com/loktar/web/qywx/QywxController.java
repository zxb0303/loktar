package com.loktar.web.qywx;

import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.dto.wx.agentmsg.AgentMsgVoice;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wxapi")
public class QywxController {

    private final QywxApi qywxApi;

    public QywxController(QywxApi qywxApi) {
        this.qywxApi = qywxApi;
    }

    @RequestMapping("/send")
    public void send() {

        String content = LokTarConstant.NOTICE_TITLE_GITHUB + "\n\n"
                        + "试试"
                        + "\n\n" + DateUtil.getMinuteSysDate();
                qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT003ID, content));


        qywxApi.sendVoiceMsg(new AgentMsgVoice(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT003ID, "3zUEeZmUuc-Eno-3qO9bDgClOpEoEL2XvqTyGpPpOqmrTswb-zG3rzsUOK8IKC5by"));
    }
}
