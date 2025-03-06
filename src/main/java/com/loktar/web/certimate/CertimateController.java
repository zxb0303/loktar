package com.loktar.web.certimate;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.certimate.Notification;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.*;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("certimate")
public class CertimateController {

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;

    public CertimateController(QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @PostMapping("/webhook.do")
    public void webhook(@RequestBody Notification notification) {
        HtmlEntityDecoderUtil.decodeHtmlEntities(notification);
        System.out.println(notification.toString());
        String content = LokTarConstant.NOTICE_CERTIMATE + System.lineSeparator() +
                System.lineSeparator() +
                notification.getMessage() + System.lineSeparator() +
                System.lineSeparator() +
                DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
    }

}
