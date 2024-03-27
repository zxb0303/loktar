package com.loktar.web.qywx;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
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

    @Value("${conf.voice.path}")
    private String voicePath;

    @Value("${conf.ffmpeg.path}")
    private String ffmpegPath;

    private final static String STR_RECEIVE = "_receive_";
    private final static String STR_REPLY = "_reply_";

    public QyWeixinCallbackChatGPTController(RedisUtil redisUtil, QywxApi qywxApi, PropertyMapper propertyMapper, QywxChatgptMsgMapper qywxChatgptMsgMapper) {
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
        this.qywxChatgptMsgMapper = qywxChatgptMsgMapper;
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
    private void asyncDealMsg(String msgSignature, String timestamp,String nonce, String xml) {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(LokTarPrivateConstant.TOEKN, LokTarPrivateConstant.ENCODINGAESKEY, LokTarPrivateConstant.CORPID);
        String xmlMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, xml);
        System.out.println("after decrypt msg: ");
        System.out.println(xmlMsg);
        Element rawRootElement = DocumentHelper.parseText(xmlMsg).getRootElement();
        String msgType = rawRootElement.element(LokTarConstant.WX_RECEICE_MSGTYPE).getTextTrim();
        ReceiveBaseMsg receiveBaseMsg;
        ReceiceMsgType type = ReceiceMsgType.getByName(msgType);
        switch (type) {
            case ReceiceMsgType.TEXT:
                receiveBaseMsg = new XmlMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).readValue(xmlMsg, ReceiveTextMsg.class);
                break;
            case ReceiceMsgType.VOICE:
                receiveBaseMsg = new XmlMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).readValue(xmlMsg, ReceiveVoiceMsg.class);
                break;
            default:
                receiveBaseMsg = new XmlMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).readValue(xmlMsg, ReceiveBaseMsg.class);
        }
        String receiveFileName = null;
        String receiveMsg = null;
        if (receiveBaseMsg instanceof ReceiveTextMsg) {
            receiveMsg = ((ReceiveTextMsg) receiveBaseMsg).getContent();
        }
        if (receiveBaseMsg instanceof ReceiveVoiceMsg) {
            receiveFileName = receiveBaseMsg.getFromUserName() + STR_RECEIVE + DateUtil.format(new Date(), DateUtil.DATEFORMATMINUTESECONDSTR);
            qywxApi.getMediaAndSave(voicePath, receiveFileName, AzureUtil.SUFFIX_AMR, ((ReceiveVoiceMsg) receiveBaseMsg).getMediaId(), receiveBaseMsg.getAgentID());
            FFmpegUtil.convertAmrToWav(ffmpegPath, voicePath + receiveFileName + AzureUtil.SUFFIX_AMR, voicePath + receiveFileName + AzureUtil.SUFFIX_WAV);
            receiveMsg = AzureUtil.wavToText(receiveFileName, voicePath);
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
        Property azureVoiceNameProperty = propertyMapper.selectByPrimaryKey("azure_voice_name");

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
        OpenAiResponse openAiResponse = ChatGPTUtil.completions(openAiRequest);

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
        String replyFileNameBase = receiveBaseMsg.getFromUserName() + STR_REPLY + DateUtil.format(date, DateUtil.DATEFORMATMINUTESECONDSTR);
        String voiceName = azureVoiceNameProperty.getValue();
        for (int i = 0; i < replyContents.size(); i++) {
            String reply = replyContents.get(i);
            String replyFileName = replyFileNameBase + "_" + (i + 1);
            AzureUtil.textToWav(replyFileName, reply, voicePath, voiceName);
            FFmpegUtil.convertWavToAmr(ffmpegPath, voicePath + replyFileName + AzureUtil.SUFFIX_WAV, voicePath + replyFileName + AzureUtil.SUFFIX_AMR);
            UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(voicePath + replyFileName + AzureUtil.SUFFIX_AMR), "voice", receiveBaseMsg.getAgentID());
            qywxApi.sendVoiceMsg(new AgentMsgVoice(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), uploadMediaRsp.getMediaId()));
        }

        //记录发出的消息
        QywxChatgptMsg replyQywxChatgptMsg = new QywxChatgptMsg();
        replyQywxChatgptMsg.setFromUserName(receiveBaseMsg.getFromUserName());
        replyQywxChatgptMsg.setRole(ChatGPTUtil.ROLE_ASSISTANT);
        replyQywxChatgptMsg.setText(replyContent);
        replyQywxChatgptMsg.setFilename(replyFileNameBase);
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
        // 正则表达式匹配任何中文句号、感叹号或问号，以及它们的转义序列
        String[] sentences = text.split("(。|！|？|\\\\n)");

        for (String sentence : sentences) {
            // 检查句子后面跟着的是什么标点符号，需要考虑可能没有标点符号的情况
            int endPosition = text.indexOf(sentence + "\\n") + sentence.length();
            String punctuation = endPosition < text.length() ? text.substring(endPosition, endPosition + 1) : "";
            // 加上相应的标点符号
            String withPunctuation = sentence + (punctuation.equals("\\") ? "\\n" : punctuation);

            if (currentChunk.length() + withPunctuation.length() <= maxLength) {
                currentChunk.append(withPunctuation);
            } else {
                // 如果当前句子本身就超过了maxLength，需要特殊处理
                if (withPunctuation.length() > maxLength) {
                    // 如果当前chunk不为空，先添加进结果
                    if (currentChunk.length() > 0) {
                        result.add(currentChunk.toString());
                        currentChunk = new StringBuilder();
                    }
                    // 将长句子拆分为小于maxLength的部分
                    while (withPunctuation.length() > maxLength) {
                        String part = withPunctuation.substring(0, maxLength);
                        result.add(part);
                        withPunctuation = withPunctuation.substring(maxLength);
                    }
                    // 将剩余部分作为新的开始
                    currentChunk.append(withPunctuation);
                } else {
                    result.add(currentChunk.toString());
                    currentChunk = new StringBuilder(withPunctuation);
                }
            }

            // 移动text中的位置到下一句，考虑到可能的转义序列
            text = text.substring(Math.min(text.length(), endPosition + (punctuation.equals("\\") ? 2 : 1)));
        }

        // 不要忘记添加最后一个块（如果有的话）
        if (currentChunk.length() > 0) {
            result.add(currentChunk.toString());
        }

        return result;
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
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(LokTarPrivateConstant.TOEKN, LokTarPrivateConstant.ENCODINGAESKEY, LokTarPrivateConstant.CORPID);
        String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp,
                nonce, echostr);
        if (sEchoStr != null) {
            return ResponseEntity.ok(sEchoStr);
        }
        return ResponseEntity.badRequest().build();
    }
}
