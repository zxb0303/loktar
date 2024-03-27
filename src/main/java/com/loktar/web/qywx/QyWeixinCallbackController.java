package com.loktar.web.qywx;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.common.Notice;
import com.loktar.dto.bandwagonhost.VPSInfo;
import com.loktar.dto.transmission.TrResponseDTO;
import com.loktar.dto.transmission.TrResponseTorrentDTO;
import com.loktar.dto.wx.BaseResult;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.dto.wx.receivemsg.*;
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

    private final NoticeServer noticeServer;

    private final QywxApi qywxApi;

    public QyWeixinCallbackController(RedisUtil redisUtil, NoticeServer noticeServer, QywxApi qywxApi) {
        this.redisUtil = redisUtil;
        this.noticeServer = noticeServer;
        this.qywxApi = qywxApi;
    }

    @PostMapping("receive.do")
    public ResponseEntity receive(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestBody String xml) {
        CompletableFuture.runAsync(() -> {
            asyncDealMsg(msgSignature, timestamp, nonce, xml);
        });
        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    private void asyncDealMsg(String msgSignature, String timestamp, String nonce, String xml) {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(LokTarPrivateConstant.TOEKN, LokTarPrivateConstant.ENCODINGAESKEY, LokTarPrivateConstant.CORPID);
        String xmlMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, xml);
        System.out.println("after decrypt msg: ");
        System.out.println(xmlMsg);
        Element rawRootElement = DocumentHelper.parseText(xmlMsg).getRootElement();
        String msgType = rawRootElement.element(LokTarConstant.WX_RECEICE_MSGTYPE).getTextTrim();
        ReceiceMsgType type = ReceiceMsgType.getByName(msgType);
        switch (type) {
            case ReceiceMsgType.TEXT:
                ReceiveTextMsg receiveTextMsg = new XmlMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).readValue(xmlMsg, ReceiveTextMsg.class);
                dealTextMsg(receiveTextMsg);
                return;
            case ReceiceMsgType.EVENT:
                ReceiveEventMsg receiveEventMsg = new XmlMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).readValue(xmlMsg, ReceiveEventMsg.class);
                dealEventMsg(receiveEventMsg);
                return;
            default:
        }
    }

    private void dealEventMsg(ReceiveEventMsg receiveEventMsg) {
        String eventKey = receiveEventMsg.getEventKey();
        EventCommandType type = EventCommandType.getByName(eventKey);
        String replymsg = "不支持该命令";
        switch (type) {
            case EventCommandType.SHOW_COMMAND:
                replymsg = "支持命令如下："
                        + "\n\n添加定时通知命令："
                        + "\n添加通知，标题，内容，时间（格式：yyyyMMddHHmm）";
                break;
            case EventCommandType.SHOW_NOTICE:
                List<Notice> notices = noticeServer.getUnsendNoticesByNoticeUser(receiveEventMsg.getFromUserName());
                replymsg = "当前待通知如下：";
                for (Notice n : notices) {
                    replymsg = replymsg + "\n\n" + n.getNoticeTitle() + "\n" + n.getNoticeContent() + "\n" + n.getNoticeTime();
                }
                return;
            case EventCommandType.SHOW_DOWNLOAD_LIST:
                replymsg = "Transmission当前下载列表：";
                TrResponseDTO trResponseDTO = TransmissionUtil.getRecentlyActiveTorrents();
                List<TrResponseTorrentDTO> trResponseTorrentDTOs = trResponseDTO.getArguments().getTorrents();
                for (TrResponseTorrentDTO trResponseTorrentDTO : trResponseTorrentDTOs) {
                    if (trResponseTorrentDTO.getStatus() == 4) {
                        replymsg = replymsg + "\n\n" + trResponseTorrentDTO.getName() + "(" + String.format("%.2f", trResponseTorrentDTO.getPercentDone() * 100) + "%)";
                    }
                }
                break;
            case EventCommandType.SHOW_CLASH_RSS:
                replymsg = "Clash订阅地址：\n\n"
                        + LokTarPrivateConstant.CLASH_RSS_URL;
            case EventCommandType.SHOW_TRANSMISSION_ALT_SPEED:
                replymsg = "Transmission当前限速状态：";
                String state = "";
                if (TransmissionUtil.getSession().getArguments().isAltSpeedEnabled()) {
                    state = "已限速";
                } else {
                    state = "未限速";
                }
                replymsg = replymsg + "\n\n" + state;
                break;
            case EventCommandType.ALT_TRANSMISSION_SPEED:
                TrResponseDTO trResponseDTOSession = TransmissionUtil.getSession();
                if (!trResponseDTOSession.getArguments().isAltSpeedEnabled()) {
                    TransmissionUtil.altSpeedEnabled(true);
                    replymsg = "Transmission已开启限速";
                } else {
                    TransmissionUtil.altSpeedEnabled(false);
                    replymsg = "Transmission已关闭限速";
                }
                break;
            case EventCommandType.SHOW_BWG_fLOW:
                replymsg = "当前搬瓦工VPS信息如下：";
                String veids[] = new String[]{"1830460"};
                for (String veid : veids) {
                    VPSInfo vpsInfo = BandwagonhostUtil.getVPSData(veid);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(vpsInfo.getDataNextReset() * 1000);
                    replymsg = replymsg + "\n\n" + vpsInfo.getHostname() + "\n"
                            + "IP：" + vpsInfo.getIpAddresses()[0] + "\n"
                            + "Bandwidth：" + vpsInfo.getDataCounter() / 1024 / 1024 / 1024 + "GB" + "/" + vpsInfo.getPlanMonthlyData() / 1024 / 1024 / 1024 + "GB" + "\n"
                            + "Reset：" + DateUtil.format(calendar.getTime(), DateUtil.DATEFORMATDAY);
                }
                break;
            case EventCommandType.UDATE_WX_MENU:
                BaseResult baseResult = qywxApi.createAgentMenu(receiveEventMsg.getAgentID());
                if (baseResult.isSuccess()) {
                    replymsg = "菜单更新成功";
                } else {
                    replymsg = "菜单更新失败";
                }
                break;
            default:
                break;
        }
        qywxApi.sendTextMsg(new AgentMsgText(receiveEventMsg.getFromUserName(), receiveEventMsg.getAgentID(), replymsg));

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
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(LokTarPrivateConstant.TOEKN, LokTarPrivateConstant.ENCODINGAESKEY, LokTarPrivateConstant.CORPID);
        String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp,
                nonce, echostr);
        if (sEchoStr != null) {
            return ResponseEntity.ok(sEchoStr);
        }
        return ResponseEntity.badRequest().build();
    }
}
