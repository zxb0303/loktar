package com.loktar.web.qywx;


import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.domain.qywx.QywxChatgptMsg;
import com.loktar.dto.common.IntentResult;
import com.loktar.dto.wx.UploadMediaRsp;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.model.chat.response.ChatResponse;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("qywx/callback/chatgpt")
@Slf4j
public class QyWeixinCallbackChatGPTController {

    private final RedisUtil redisUtil;

    private final QywxApi qywxApi;

    private final PropertyMapper propertyMapper;

    private final QywxChatgptMsgMapper qywxChatgptMsgMapper;

    private final AzureVoiceUtil azureVoiceUtil;

    private final ChatGPTUtil chatGPTUtil;

    private final LokTarConfig lokTarConfig;

    private final FFmpegUtil ffmpegUtil;

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    private final static ObjectMapper xmlMapper = new XmlMapper();

    private static final Duration NOTICE_DRAFT_TTL = Duration.ofMinutes(10);

    public QyWeixinCallbackChatGPTController(RedisUtil redisUtil, QywxApi qywxApi, PropertyMapper propertyMapper, QywxChatgptMsgMapper qywxChatgptMsgMapper, AzureVoiceUtil azureVoiceUtil, ChatGPTUtil chatGPTUtil, LokTarConfig lokTarConfig, FFmpegUtil ffmpegUtil, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.redisUtil = redisUtil;
        this.qywxApi = qywxApi;
        this.propertyMapper = propertyMapper;
        this.qywxChatgptMsgMapper = qywxChatgptMsgMapper;
        this.azureVoiceUtil = azureVoiceUtil;
        this.chatGPTUtil = chatGPTUtil;
        this.lokTarConfig = lokTarConfig;
        this.ffmpegUtil = ffmpegUtil;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
    }

    @PostMapping("receive")
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
        log.info("{}", "after decrypt msg: ");
        log.info("{}", xmlMsg);
        String msgType = xmlMapper.readTree(xmlMsg).get(LokTarConstant.WX_RECEIVE_MSGTYPE).asText().trim();
        ReceiveBaseMsg receiveBaseMsg;
        ReceiceMsgType type = ReceiceMsgType.getByName(msgType);
        receiveBaseMsg = switch (type) {
            case ReceiceMsgType.TEXT -> xmlMapper.readValue(xmlMsg, ReceiveTextMsg.class);
            case ReceiceMsgType.VOICE -> xmlMapper.readValue(xmlMsg, ReceiveVoiceMsg.class);
            default -> xmlMapper.readValue(xmlMsg, ReceiveBaseMsg.class);
        };
        String receiveFileName = null;
        String receiveMsg = null;
        if (receiveBaseMsg instanceof ReceiveTextMsg) {
            receiveMsg = ((ReceiveTextMsg) receiveBaseMsg).getContent();
        }
        if (receiveBaseMsg instanceof ReceiveVoiceMsg) {
            receiveFileName = qywxApi.saveMedia(lokTarConfig.getPath().getVoice(), ((ReceiveVoiceMsg) receiveBaseMsg).getMediaId(), receiveBaseMsg.getAgentID());
            ffmpegUtil.convertAmrToWav(lokTarConfig.getPath().getVoice(), receiveFileName);
            testFileExist(lokTarConfig.getPath().getVoice(), receiveFileName);
            receiveMsg = azureVoiceUtil.wavToText(lokTarConfig.getPath().getVoice(), receiveFileName);
            qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), "语音识别结果：\n" + receiveMsg));
        }
        dealWitchChatGPT(receiveFileName, receiveMsg, receiveBaseMsg);
    }

    private void dealWitchChatGPT(String receiveFileName, String receiveMsg, ReceiveBaseMsg receiveBaseMsg) {
        if (ObjectUtils.isEmpty(receiveMsg)) {
            log.info("{}", "content为空");
            return;
        }
        Property chatgptModelProperty = propertyMapper.selectByPrimaryKey("chatgpt_model");

        //记录收到的消息
        QywxChatgptMsg receiveQywxChatgptMsg = new QywxChatgptMsg();
        receiveQywxChatgptMsg.setFromUserName(receiveBaseMsg.getFromUserName());
        receiveQywxChatgptMsg.setAgentId(receiveBaseMsg.getAgentID());
        receiveQywxChatgptMsg.setRole(ChatMessageType.USER.name());
        receiveQywxChatgptMsg.setText(receiveMsg);
        receiveQywxChatgptMsg.setFilename(receiveFileName);
        receiveQywxChatgptMsg.setCreateTime(LocalDateTime.now());
        qywxChatgptMsgMapper.insert(receiveQywxChatgptMsg);

        // 先尝试获取提醒草稿，支持多轮补充信息
        IntentResult noticeDraft = getNoticeDraft(receiveBaseMsg.getFromUserName());

        // AI 判断会话意图
        IntentResult noticeIntent = chatGPTUtil.extractIntent(receiveMsg, noticeDraft, chatgptModelProperty.getValue(), receiveBaseMsg.getFromUserName());

        // 重置会话
        if (noticeIntent.isResetSession()) {
            chatGPTUtil.resetChat(receiveBaseMsg.getFromUserName());
            clearNoticeDraft(receiveBaseMsg.getFromUserName());
            sendTextAndVoice("会话已重置", receiveBaseMsg, true, false);
            return;
        }

        if (noticeIntent.isNotice()) {
            if (noticeIntent.isNeedConfirm()) {
                saveNoticeDraft(receiveBaseMsg.getFromUserName(), noticeIntent);
                String confirmReply = chatGPTUtil.buildNoticeConfirmReply(noticeIntent, chatgptModelProperty.getValue());
                sendTextAndVoice(confirmReply, receiveBaseMsg, true, true);
                return;
            }
            clearNoticeDraft(receiveBaseMsg.getFromUserName());
            String replymsg = noticeIntent.isInsertSuccess() ? "已添加提醒：" + noticeIntent.getTitle() : "添加提醒失败";
            sendTextAndVoice(replymsg, receiveBaseMsg, true, false);
            return;
        }

        ChatResponse chatResponse = chatGPTUtil.chat(receiveBaseMsg.getFromUserName(), receiveMsg, chatgptModelProperty.getValue());
        if (ObjectUtils.isEmpty(chatResponse)) {
            qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), "token已达上限，请重置会话"));
            return;
        }
        String replyContent = chatResponse.aiMessage().text();
        sendTextAndVoice(replyContent, receiveBaseMsg, true, true);
    }

    private void sendTextAndVoice(String replyContent, ReceiveBaseMsg receiveBaseMsg, boolean sendText, boolean sendVoice) {
        String replyFileNameBase = "";
        replyContent = replyContent.replace("\n\n", "").replace("*", "");
        List<String> replyContents = splitTextBySentence(replyContent);
        if (sendText) {
            for (String content : replyContents) {
                qywxApi.sendTextMsg(new AgentMsgText(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), content));
            }
        }
        if (sendVoice) {
            replyFileNameBase = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_FILENAME);
            for (int i = 0; i < replyContents.size(); i++) {
                String reply = replyContents.get(i);
                String wavFileName = replyFileNameBase + "_" + (i + 1) + LokTarConstant.VOICE_SUFFIX_WAV;
                azureVoiceUtil.textToWav(lokTarConfig.getPath().getVoice(), wavFileName, reply);
                ffmpegUtil.convertWavToAmr(lokTarConfig.getPath().getVoice(), wavFileName);
                testFileExist(lokTarConfig.getPath().getVoice(), wavFileName);
                UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(lokTarConfig.getPath().getVoice() + wavFileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR)), receiveBaseMsg.getAgentID());
                qywxApi.sendVoiceMsg(new AgentMsgVoice(receiveBaseMsg.getFromUserName(), receiveBaseMsg.getAgentID(), uploadMediaRsp.getMediaId()));
            }
        }
        //记录发出的消息
        QywxChatgptMsg replyQywxChatgptMsg = new QywxChatgptMsg();
        replyQywxChatgptMsg.setFromUserName(receiveBaseMsg.getFromUserName());
        replyQywxChatgptMsg.setAgentId(receiveBaseMsg.getAgentID());
        replyQywxChatgptMsg.setRole(ChatMessageType.AI.name());
        replyQywxChatgptMsg.setText(replyContent);
        if (!replyFileNameBase.isBlank()) {
            replyQywxChatgptMsg.setFilename(replyFileNameBase + LokTarConstant.VOICE_SUFFIX_WAV);
        }
        replyQywxChatgptMsg.setCreateTime(LocalDateTime.now());
        qywxChatgptMsgMapper.insert(replyQywxChatgptMsg);
    }

    private String buildNoticeDraftKey(String userId) {
        return LokTarConstant.REDIS_KEY_PREFIX_NOTICE_DRAFT + userId;
    }

    private void saveNoticeDraft(String userId, IntentResult draft) {
        try {
            stringRedisTemplate.opsForValue().set(buildNoticeDraftKey(userId), objectMapper.writeValueAsString(draft), NOTICE_DRAFT_TTL);
        } catch (JsonProcessingException e) {
            log.error("保存提醒草稿失败: {}", e.getMessage(), e);
        }
    }

    private IntentResult getNoticeDraft(String userId) {
        String json = stringRedisTemplate.opsForValue().get(buildNoticeDraftKey(userId));
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, IntentResult.class);
        } catch (JsonProcessingException e) {
            log.warn("读取提醒草稿失败，清空草稿: {}", e.getMessage());
            clearNoticeDraft(userId);
            return null;
        }
    }

    private void clearNoticeDraft(String userId) {
        stringRedisTemplate.delete(buildNoticeDraftKey(userId));
    }

    public static List<String> splitTextBySentence(String text) {
        int maxLength = 280;
        List<String> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        // 使用正向预查保留分隔符
        String[] sentences = text.split("(?<=[。！？\\r\\n\\u0085\\u2028\\u2029])");

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() <= maxLength) {
                currentChunk.append(sentence);
            } else {
                if (!currentChunk.isEmpty()) {
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

        if (!currentChunk.isEmpty()) {
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
            File file = new File(lokTarConfig.getPath().getVoice() + coverFileName);
            if (file.exists()) {
                //System.out.println("file exist "+DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
                break;
            }
            //System.out.println("file not exist "+DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
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
    @GetMapping("receive")
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
