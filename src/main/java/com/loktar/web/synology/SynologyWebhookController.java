package com.loktar.web.synology;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("synology")
public class SynologyWebhookController {
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    public SynologyWebhookController(QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @RequestMapping("/sendMsg.do")
    public void send(String title,String text,String touser) {
        String content = new StringBuilder().append(title).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(text).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(DateUtil.getMinuteSysDate()).toString();
        qywxApi.sendTextMsg(new AgentMsgText(touser, lokTarConfig.qywxAgent002Id, content));
    }
}
