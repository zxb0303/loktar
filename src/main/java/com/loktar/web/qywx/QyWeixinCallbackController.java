package com.loktar.web.qywx;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Notice;
import com.loktar.domain.transmission.TrTorrent;
import com.loktar.dto.bandwagonhost.VPSInfo;
import com.loktar.dto.transmission.TrResponse;
import com.loktar.dto.wx.BaseResult;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.dto.wx.receivemsg.*;
import com.loktar.mapper.transmission.TrTorrentMapper;
import com.loktar.service.common.NoticeServer;
import com.loktar.util.BandwagonhostUtil;
import com.loktar.util.DateUtil;
import com.loktar.util.RedisUtil;
import com.loktar.util.TransmissionUtil;
import com.loktar.util.wx.aes.WXBizMsgCrypt;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("qywx/callback")
public class QyWeixinCallbackController {

    private final RedisUtil redisUtil;

    private final TransmissionUtil transmissionUtil;

    private final NoticeServer noticeServer;

    private final QywxApi qywxApi;

    private final BandwagonhostUtil bandwagonhostUtil;

    private final LokTarConfig lokTarConfig;

    private final TrTorrentMapper trTorrentMapper;

    private final static ObjectMapper xmlMapper = new XmlMapper();


    public QyWeixinCallbackController(RedisUtil redisUtil, TransmissionUtil transmissionUtil, NoticeServer noticeServer, QywxApi qywxApi, BandwagonhostUtil bandwagonhostUtil, LokTarConfig lokTarConfig, TrTorrentMapper trTorrentMapper) {
        this.redisUtil = redisUtil;
        this.transmissionUtil = transmissionUtil;
        this.noticeServer = noticeServer;
        this.qywxApi = qywxApi;
        this.bandwagonhostUtil = bandwagonhostUtil;
        this.lokTarConfig = lokTarConfig;
        this.trTorrentMapper = trTorrentMapper;
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
    }

    @PostMapping("receive.do")
    public ResponseEntity receive(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestBody String xml) {
        if (!redisUtil.setIfAbsent(msgSignature,timestamp, 30)) {
            return ResponseEntity.noContent().build();
        }
        CompletableFuture.runAsync(() -> {
            try {
                asyncDealMsg(msgSignature, timestamp, nonce, xml);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.noContent().build();
    }
    @SneakyThrows
    private void asyncDealMsg(String msgSignature, String timestamp, String nonce, String xml) {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(lokTarConfig.qywxToken, lokTarConfig.qywxEncodingAESKey, lokTarConfig.qywxCorpId);
        String xmlMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, xml);
        System.out.println("after decrypt msg: ");
        System.out.println(xmlMsg);
        Element rawRootElement = DocumentHelper.parseText(xmlMsg).getRootElement();
        String msgType = rawRootElement.element(LokTarConstant.WX_RECEICE_MSGTYPE).getTextTrim();
        ReceiceMsgType type = ReceiceMsgType.getByName(msgType);
        switch (type) {
            case ReceiceMsgType.TEXT:
                ReceiveTextMsg receiveTextMsg = xmlMapper.readValue(xmlMsg, ReceiveTextMsg.class);
                dealTextMsg(receiveTextMsg);
                return;
            case ReceiceMsgType.EVENT:
                ReceiveEventMsg receiveEventMsg = xmlMapper.readValue(xmlMsg, ReceiveEventMsg.class);
                dealEventMsg(receiveEventMsg);
                return;
            default:
        }
    }

    private void dealEventMsg(ReceiveEventMsg receiveEventMsg) {
        String eventKey = receiveEventMsg.getEventKey();
        EventCommandType type = EventCommandType.getByName(eventKey);
        StringBuilder replymsg = new StringBuilder();
        switch (type) {
            case EventCommandType.SHOW_COMMAND:
                replymsg.append("支持命令如下：").append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append("添加定时通知命令：").append(System.lineSeparator())
                        .append("添加通知，标题，内容，时间(格式：yyyyMMddHHmm)").append(System.lineSeparator());
                break;
            case EventCommandType.SHOW_NOTICE:
                List<Notice> notices = noticeServer.getUnsendNoticesByNoticeUser(receiveEventMsg.getFromUserName());
                replymsg.append("当前待通知如下：").append(System.lineSeparator())
                        .append(System.lineSeparator());
                for (Notice n : notices) {
                    replymsg.append(n.getNoticeTitle()).append(System.lineSeparator())
                            .append(n.getNoticeContent()).append(System.lineSeparator())
                            .append(n.getNoticeTime()).append(System.lineSeparator())
                            .append(System.lineSeparator());
                }
                break;
            case EventCommandType.SHOW_DOWNLOAD_LIST:
                replymsg.append("Transmission当前下载列表：").append(System.lineSeparator())
                        .append(System.lineSeparator());
                List<TrTorrent> torrents = trTorrentMapper.getTrTorrentsByStatus(4);
                for (TrTorrent trTorrent : torrents) {
                    replymsg.append(trTorrent.getName()).append(System.lineSeparator())
                            .append("(" + String.format("%.2f", trTorrent.getPercentDone() * 100) + "%)").append(System.lineSeparator());
                }
                break;
            case EventCommandType.SHOW_CLASH_RSS:
                replymsg.append("Clash订阅地址：").append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(lokTarConfig.commonClashRssUrl).append(System.lineSeparator());
                break;
            case EventCommandType.SHOW_TRANSMISSION_ALT_SPEED:
                replymsg.append("Transmission当前限速状态：").append(System.lineSeparator());
                String state = "";
                if (transmissionUtil.getSession().getArguments().getAltSpeedEnabled()) {
                    state = "已限速";
                } else {
                    state = "未限速";
                }
                replymsg.append(System.lineSeparator())
                        .append(state).append(System.lineSeparator());
                break;
            case EventCommandType.ALT_TRANSMISSION_SPEED:
                TrResponse trResponseSession = transmissionUtil.getSession();
                if (!trResponseSession.getArguments().getAltSpeedEnabled()) {
                    transmissionUtil.altSpeedEnabled(true);
                    replymsg.append("Transmission已开启限速").append(System.lineSeparator());
                } else {
                    transmissionUtil.altSpeedEnabled(false);
                    replymsg.append("Transmission已关闭限速").append(System.lineSeparator());
                }
                break;
            case EventCommandType.SHOW_BWG_fLOW:
                replymsg.append("当前搬瓦工VPS信息如下：").append(System.lineSeparator());
                String veids[] = new String[]{"1830460"};
                for (String veid : veids) {
                    VPSInfo vpsInfo = bandwagonhostUtil.getVPSData(veid);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(vpsInfo.getDataNextReset() * 1000);
                    replymsg.append(System.lineSeparator())
                            .append(vpsInfo.getHostname()).append(System.lineSeparator())
                            .append("IP：").append(vpsInfo.getIpAddresses()[0]).append(System.lineSeparator())
                            .append("Bandwidth：").append(vpsInfo.getDataCounter() / 1024 / 1024 / 1024 + "GB" + "/" + vpsInfo.getPlanMonthlyData() / 1024 / 1024 / 1024 + "GB").append(System.lineSeparator())
                            .append("Reset：").append(DateUtil.format(calendar.getTime(), DateUtil.DATEFORMATDAY)).append(System.lineSeparator());
                }
                break;
            case EventCommandType.UDATE_WX_MENU:
                BaseResult baseResult = qywxApi.createAgentMenu(receiveEventMsg.getAgentID());
                if (baseResult.getErrcode() == 0) {
                    replymsg.append("菜单更新成功").append(System.lineSeparator());
                } else {
                    replymsg.append("菜单更新失败").append(System.lineSeparator());
                }
                break;
            default:
                replymsg.append("不支持该命令").append(System.lineSeparator());
                break;
        }
        qywxApi.sendTextMsg(new AgentMsgText(receiveEventMsg.getFromUserName(), receiveEventMsg.getAgentID(), replymsg.toString()));

    }

    private void dealTextMsg(ReceiveTextMsg receiveTextMsg) {
        String content = receiveTextMsg.getContent();
        if (ObjectUtils.isEmpty(content)) {
            System.out.println("content为空");
            return;
        }
        content = content.toLowerCase().replace(" ", "").replace("，", ",");
        String[] strs = content.split(",");
        String commandType = strs[0];
        TextCommandType type = TextCommandType.getByName(commandType);
        switch (type) {
            case TextCommandType.ADD_NOTICE:
                Notice notice = new Notice();
                notice.setNoticeTitle(strs[1]);
                notice.setNoticeContent(strs[2]);
                notice.setNoticeTime(DateUtil.format(DateUtil.parase(strs[3], DateUtil.DATEFORMATMINUTESTR), DateUtil.DATEFORMATMINUTE));
                notice.setNoticeUser(receiveTextMsg.getFromUserName());
                int result = noticeServer.insert(notice);
                String replymsg = "添加通知失败";
                if (result == 1) {
                    replymsg = "添加通知成功";
                }
                qywxApi.sendTextMsg(new AgentMsgText(receiveTextMsg.getFromUserName(), receiveTextMsg.getAgentID(), replymsg));
                break;
            default:
                break;
        }
    }


    @SneakyThrows
    @GetMapping("receive.do")
    public ResponseEntity msgValid(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(lokTarConfig.qywxToken, lokTarConfig.qywxEncodingAESKey, lokTarConfig.qywxCorpId);
        String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp,
                nonce, echostr);
        if (sEchoStr != null) {
            return ResponseEntity.ok(sEchoStr);
        }
        return ResponseEntity.badRequest().build();
    }
}
