package com.loktar.web.synology;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("synology")
public class SynologyWebhookController {
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    public SynologyWebhookController(QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @GetMapping("/sendMsg.do")
    public void send(String title,String text,String touser) {
        String content = title + System.lineSeparator() +
                System.lineSeparator() +
                text + System.lineSeparator() +
                System.lineSeparator() +
                DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
        qywxApi.sendTextMsg(new AgentMsgText(touser, lokTarConfig.getQywx().getAgent002Id(), content));
    }
}
