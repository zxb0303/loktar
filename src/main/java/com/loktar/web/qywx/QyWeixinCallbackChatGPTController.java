package com.loktar.web.qywx;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.domain.qywx.QywxChatgptMsg;
import com.loktar.dto.openai.OpenAiMessage;
import com.loktar.dto.openai.OpenAiRequest;
import com.loktar.dto.openai.OpenAiResponse;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.dto.wx.agentmsg.AgentMsgVoice;
import com.loktar.dto.wx.receivemsg.ReceiceMsgType;
import com.loktar.dto.wx.receivemsg.ReceiveBaseMsg;
import com.loktar.dto.wx.receivemsg.ReceiveTextMsg;
import com.loktar.dto.wx.receivemsg.ReceiveVoiceMsg;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.mapper.qywx.QywxChatgptMsgMapper;
import com.loktar.util.*;
import com.loktar.util.wx.aes.WXBizMsgCrypt;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("qywx/callback/chatgpt")
public class QyWeixinCallbackChatGPTController {

    private final RedisUtil redisUtil;

    private final QywxApi qywxApi;

    private final PropertyMapper propertyMapper;

    private final QywxChatgptMsgMapper qywxChatgptMsgMapper;

    private final AzureVoiceUtil azureVoiceUtil;

    private final ChatGPTUtil chatGPTUtil;

    private final LokTarConfig lokTarConfig;

    private final static ObjectMapper xmlMapper = new XmlMapper();



    @Value("${conf.voice.path}")
    private String voicePath;

    public QyWeixinCallbackChatGPTController(RedisUtil redisUtil, QywxApi qywxApi, PropertyMapper propertyMapper, QywxChatgptMsgMapper qywxChatgptMsgMapper, AzureVoiceUtil azureVoiceUtil, ChatGPTUtil chatGPTUtil, LokTarConfig lokTarConfig) {
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
        this.qywxChatgptMsgMapper = qywxChatgptMsgMapper;
        this.azureVoiceUtil = azureVoiceUtil;
        this.chatGPTUtil = chatGPTUtil;
        this.lokTarConfig = lokTarConfig;
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
        ReceiveBaseMsg receiveBaseMsg;
        ReceiceMsgType type = ReceiceMsgType.getByName(msgType);
        switch (type) {
            case ReceiceMsgType.TEXT:
                receiveBaseMsg = xmlMapper.readValue(xmlMsg, ReceiveTextMsg.class);
                break;
            case ReceiceMsgType.VOICE:
                receiveBaseMsg = xmlMapper.readValue(xmlMsg, ReceiveVoiceMsg.class);
                break;
            default:
                receiveBaseMsg = xmlMapper.readValue(xmlMsg, ReceiveBaseMsg.class);
        }
        String receiveFileName = null;
        String receiveMsg = null;
        if (receiveBaseMsg instanceof ReceiveTextMsg) {
            receiveMsg = ((ReceiveTextMsg) receiveBaseMsg).getContent();
        }
        if (receiveBaseMsg instanceof ReceiveVoiceMsg) {
            receiveFileName = qywxApi.saveMedia(voicePath, ((ReceiveVoiceMsg) receiveBaseMsg).getMediaId(), receiveBaseMsg.getAgentID());
            FFmpegUtil.convertAmrToWav(voicePath, receiveFileName);
            testFileExist(voicePath,receiveFileName);
            receiveMsg = azureVoiceUtil.wavToText(voicePath, receiveFileName);
            qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), "语音识别结果：\n" + receiveMsg));
        }
        dealWitchChatGPT(receiveFileName, receiveMsg, receiveBaseMsg);
    }

    private void dealWitchChatGPT(String receiveFileName, String receiveMsg, ReceiveBaseMsg receiveBaseMsg) {
        if (ObjectUtils.isEmpty(receiveMsg)) {
            System.out.println("content为空");
            return;
        }
        Property chatgptModelProperty = propertyMapper.selectByPrimaryKey("chatgpt_model");
        Property chatgptMaxTokensProperty = propertyMapper.selectByPrimaryKey("chatgpt_maxtoken");

        //记录收到的消息
        QywxChatgptMsg receiveQywxChatgptMsg = new QywxChatgptMsg();
        receiveQywxChatgptMsg.setFromUserName(receiveBaseMsg.getFromUserName());
        receiveQywxChatgptMsg.setRole(ChatGPTUtil.ROLE_USER);
        receiveQywxChatgptMsg.setText(receiveMsg);
        receiveQywxChatgptMsg.setFilename(receiveFileName);
        receiveQywxChatgptMsg.setPromptTokens(0);
        receiveQywxChatgptMsg.setCompletionTokens(0);
        receiveQywxChatgptMsg.setTotaltokens(0);
        receiveQywxChatgptMsg.setCreateTime(new Date());
        qywxChatgptMsgMapper.insert(receiveQywxChatgptMsg);

        if (receiveMsg.equals("重置会话")) {
            redisUtil.del(LokTarConstant.REDIS_KEY_PREFIX_OPENAI_REQUEST + receiveBaseMsg.getFromUserName());
            qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), "会话已重置"));
            return;
        }

        OpenAiRequest openAiRequest = (OpenAiRequest) redisUtil.get(LokTarConstant.REDIS_KEY_PREFIX_OPENAI_REQUEST + receiveBaseMsg.getFromUserName());
        if (ObjectUtils.isEmpty(openAiRequest)) {
            openAiRequest = ChatGPTUtil.getDefalutRequest();
            openAiRequest.setModel(chatgptModelProperty.getValue());
            openAiRequest.setMaxTokens(Integer.valueOf(chatgptMaxTokensProperty.getValue()));
        }
        OpenAiMessage openAiMessage = new OpenAiMessage(ChatGPTUtil.ROLE_USER, receiveMsg);
        openAiRequest.getMessages().add(openAiMessage);
        OpenAiResponse openAiResponse = chatGPTUtil.completions(openAiRequest);

        if (ObjectUtils.isEmpty(openAiResponse)) {
            qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), "token已达上限，请重置会话"));
            return;
        }
        OpenAiMessage replyMsg = openAiResponse.getChoices().get(0).getMessage();
        openAiRequest.getMessages().add(replyMsg);
        redisUtil.set(LokTarConstant.REDIS_KEY_PREFIX_OPENAI_REQUEST + receiveBaseMsg.getFromUserName(), openAiRequest, 3600);

        String replyContent = replyMsg.getContent();
        replyContent = replyContent.replace("\n\n", "");
        List<String> replyContents = splitTextBySentence(replyContent);

        for (int j = 0; j < replyContents.size(); j++) {
            qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), replyContents.get(j)));
        }

        Date date = new Date();
        String replyFileNameBase = DateUtil.format(date, DateUtil.DATEFORMATMINUTESECONDSTR);
        for (int i = 0; i < replyContents.size(); i++) {
            String reply = replyContents.get(i);
            String wavFileName = replyFileNameBase + "_" + (i + 1) + LokTarConstant.VOICE_SUFFIX_WAV;
            azureVoiceUtil.textToWav(voicePath, wavFileName, reply);
            FFmpegUtil.convertWavToAmr(voicePath, wavFileName);
            testFileExist(voicePath,wavFileName);
            UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(voicePath + wavFileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR)), receiveBaseMsg.getAgentID());
            qywxApi.sendVoiceMsg(new AgentMsgVoice(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), uploadMediaRsp.getMediaId()));
        }

        //记录发出的消息
        QywxChatgptMsg replyQywxChatgptMsg = new QywxChatgptMsg();
        replyQywxChatgptMsg.setFromUserName(receiveBaseMsg.getFromUserName());
        replyQywxChatgptMsg.setRole(ChatGPTUtil.ROLE_ASSISTANT);
        replyQywxChatgptMsg.setText(replyContent);
        replyQywxChatgptMsg.setFilename(replyFileNameBase + LokTarConstant.VOICE_SUFFIX_WAV);
        replyQywxChatgptMsg.setPromptTokens(openAiResponse.getUsage().getPromptTokens());
        replyQywxChatgptMsg.setCompletionTokens(openAiResponse.getUsage().getCompletionTokens());
        replyQywxChatgptMsg.setTotaltokens(openAiResponse.getUsage().getTotalTokens());
        replyQywxChatgptMsg.setCreateTime(new Date());
        qywxChatgptMsgMapper.insert(replyQywxChatgptMsg);
    }

    public static List<String> splitTextBySentence(String text) {
        int maxLength = 280;
        List<String> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        // 使用正向预查保留分隔符
        String[] sentences = text.split("(?<=。|！|？|\n)");

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() <= maxLength) {
                currentChunk.append(sentence);
            } else {
                if (currentChunk.length() > 0) {
                    result.add(currentChunk.toString());
                    currentChunk = new StringBuilder();
                }
                // 处理单个句子长度超过最大长度的情况
                while (sentence.length() > maxLength) {
                    String part = sentence.substring(0, maxLength);
                    result.add(part);
                    sentence = sentence.substring(maxLength);
                }
                currentChunk.append(sentence);
            }
        }

        if (currentChunk.length() > 0) {
            result.add(currentChunk.toString());
        }

        return result;
    }

    @SneakyThrows
    private void testFileExist(String voicePath, String fileName) {
        //System.out.println("fileName："+fileName);
        String coverFileName = fileName.lastIndexOf(LokTarConstant.VOICE_SUFFIX_WAV) != -1 ? fileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR) : fileName.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV);
        //System.out.println(coverFileName);
        int times = 10;
         while (times > 0) {
            File file = new File(voicePath + coverFileName);
            if (file.exists()) {
                //System.out.println("file exist "+DateUtil.getTodayToSecond());
                break;
            }
             //System.out.println("file not exist "+DateUtil.getTodayToSecond());
             times--;
             Thread.sleep(1000);
        }
    }

    /**
     * 消息验证
     *
     * @param msgSignature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
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
