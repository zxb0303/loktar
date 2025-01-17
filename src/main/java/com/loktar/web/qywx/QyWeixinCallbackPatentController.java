package com.loktar.web.qywx;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.qywx.QywxPatentMsg;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.dto.wx.receivemsg.ReceiceMsgType;
import com.loktar.dto.wx.receivemsg.ReceiveTextMsg;
import com.loktar.mapper.patent.PatentApplyMapper;
import com.loktar.mapper.qywx.QywxPatentMsgMapper;
import com.loktar.util.RedisUtil;
import com.loktar.util.wx.aes.WXBizMsgCrypt;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;


@RestController
@RequestMapping("qywx/callback/patent")
public class QyWeixinCallbackPatentController {

    private final RedisUtil redisUtil;

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;

    private final QywxPatentMsgMapper qywxPatentMsgMapper;

    private final PatentApplyMapper patentApplyMapper;

    private final static ObjectMapper xmlMapper = new XmlMapper();


    public QyWeixinCallbackPatentController(RedisUtil redisUtil, QywxApi qywxApi, LokTarConfig lokTarConfig, QywxPatentMsgMapper qywxPatentMsgMapper, PatentApplyMapper patentApplyMapper) {
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
        this.qywxPatentMsgMapper = qywxPatentMsgMapper;
        this.patentApplyMapper = patentApplyMapper;
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
    }

    @PostMapping("receive.do")
    public ResponseEntity<Void> receive(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestBody String xml) {
        if (!redisUtil.setIfAbsent(msgSignature, timestamp, 30)) {
            return ResponseEntity.noContent().build();
        }
        Thread.ofVirtual().start(() -> asyncDealMsg(msgSignature, timestamp, nonce, xml));
        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    private void asyncDealMsg(String msgSignature, String timestamp, String nonce, String xml) {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(lokTarConfig.getQywx().getToken(), lokTarConfig.getQywx().getEncodingAeskey(), lokTarConfig.getQywx().getCorpid());
        String xmlMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, xml);
        System.out.println("after decrypt msg: ");
        System.out.println(xmlMsg);
        Element rawRootElement = DocumentHelper.parseText(xmlMsg).getRootElement();
        String msgType = rawRootElement.element(LokTarConstant.WX_RECEIVE_MSGTYPE).getTextTrim();
        ReceiceMsgType type = ReceiceMsgType.getByName(msgType);
        switch (type) {
            case ReceiceMsgType.TEXT:
                ReceiveTextMsg receiveTextMsg = xmlMapper.readValue(xmlMsg, ReceiveTextMsg.class);
                dealTextMsg(receiveTextMsg);
                return;
            default:
        }
    }

    private void dealTextMsg(ReceiveTextMsg receiveTextMsg) {
        String content = receiveTextMsg.getContent();
        if (ObjectUtils.isEmpty(content) || !content.contains("公司")) {
            System.out.println("content为空");
            qywxApi.sendTextMsg(new AgentMsgText(receiveTextMsg.getFromUserName(), receiveTextMsg.getAgentID(), "请提供公司名称"));
            return;
        }
        content = content.trim();
        content = content.replaceAll("\\s+", " ").replace(" ", ",").replace("，", ",");
        QywxPatentMsg qywxPatentMsg = new QywxPatentMsg();
        qywxPatentMsg.setAgentId(receiveTextMsg.getAgentID());
        qywxPatentMsg.setFromUserName(receiveTextMsg.getFromUserName());
        qywxPatentMsg.setContent(receiveTextMsg.getContent());
        qywxPatentMsg.setStatus(LokTarConstant.QYWX_PATENT_MSG_STATUS_RECEIVED);
        if (!content.contains(",")) {
            qywxPatentMsg.setType(LokTarConstant.QYWX_PATENT_MSG_TYPE_QUOTATION);
            qywxPatentMsg.setApplyName(content);
            qywxPatentMsg.setPrice(LokTarConstant.PATENT_DEFAULT_PRICE);
        }
        if (content.contains(",")) {
            String[] split = content.split(",");
            if (isInteger(split[0])) {
                qywxPatentMsg.setType(LokTarConstant.QYWX_PATENT_MSG_TYPE_QUOTATION);
                qywxPatentMsg.setApplyName(split[1]);
                qywxPatentMsg.setPrice(split[0]);
            } else {
                qywxPatentMsg.setType(LokTarConstant.QYWX_PATENT_MSG_TYPE_CONTRACT);
                qywxPatentMsg.setApplyName(split[0]);
                qywxPatentMsg.setPrice(split[1]);
                if (split.length >= 3) {
                    qywxPatentMsg.setMobile(split[2]);
                }
            }
        }

        PatentApply patentApply = patentApplyMapper.selectByApplyName(qywxPatentMsg.getApplyName());
        if (ObjectUtils.isEmpty(patentApply)) {
            qywxApi.sendTextMsg(new AgentMsgText(receiveTextMsg.getFromUserName(), receiveTextMsg.getAgentID(), "请提供公司名称"));
            return;
        }
        String sendMsg = "";
        if (qywxPatentMsg.getType().equals(LokTarConstant.QYWX_PATENT_MSG_TYPE_QUOTATION)) {
            sendMsg = MessageFormat.format(LokTarConstant.PATENT_NOTICE_MSG_QUOTATION, qywxPatentMsg.getApplyName(), qywxPatentMsg.getPrice());
        } else {
            sendMsg = MessageFormat.format(LokTarConstant.PATENT_NOTICE_MSG_CONTRACT, qywxPatentMsg.getApplyName(), qywxPatentMsg.getPrice());
        }
        qywxApi.sendTextMsg(new AgentMsgText(receiveTextMsg.getFromUserName(), receiveTextMsg.getAgentID(), sendMsg));
        sendMsg = qywxPatentMsg.getFromUserName() + " " + sendMsg;
        if (!qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeZxb())) {
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), receiveTextMsg.getAgentID(), sendMsg));
        }
        if (!qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeCxy())) {
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), receiveTextMsg.getAgentID(), sendMsg));
        }
        //这里的删除代码好像不对 已改权限再测一下
//        if (qywxPatentMsg.getType().equals("01")) {
//            File file = new File(lokTarConfig.getPath().getPatent() + "quotation/" + qywxPatentMsg.getApplyName() + ".xlsx");
//            if (file.exists()) {
//                file.delete();
//            }
//        }
//        if (qywxPatentMsg.getType().equals("02")) {
//            File file1 = new File(lokTarConfig.getPath().getPatent() + "contract/收购合同-" + qywxPatentMsg.getApplyName() + ".doc");
//            File file2 = new File(lokTarConfig.getPath().getPatent() + "contract/转让协议-" + qywxPatentMsg.getApplyName() + ".doc");
//            if (file1.exists()) {
//                file1.delete();
//            }
//            if (file2.exists()) {
//                file2.delete();
//            }
//        }
        qywxPatentMsgMapper.insert(qywxPatentMsg);
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SneakyThrows
    @GetMapping("receive.do")
    public ResponseEntity<String> msgValid(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(lokTarConfig.getQywx().getToken(), lokTarConfig.getQywx().getEncodingAeskey(), lokTarConfig.getQywx().getCorpid());
        String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp,
                nonce, echostr);
        if (sEchoStr != null) {
            return ResponseEntity.ok(sEchoStr);
        }
        return ResponseEntity.badRequest().build();
    }
}
