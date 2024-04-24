package com.loktar.web.openai;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.openai.OpenAiMessage;
import com.loktar.dto.openai.OpenAiRequest;
import com.loktar.dto.openai.OpenAiResponse;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgVoice;
import com.loktar.util.*;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;

@RestController
@RequestMapping("chatgpt")
public class ChatGPTController {
    private OpenAiRequest openAiRequest;

    private final QywxApi qywxApi;

    private final AzureVoiceUtil azureVoiceUtil;

    private final ChatGPTUtil chatGPTUtil;

    private final LokTarConfig lokTarConfig;

    @Value("${conf.voice.path}")
    private String voicePath;

    public ChatGPTController(QywxApi qywxApi, AzureVoiceUtil azureVoiceUtil, ChatGPTUtil chatGPTUtil, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.azureVoiceUtil = azureVoiceUtil;
        this.chatGPTUtil = chatGPTUtil;
        this.lokTarConfig = lokTarConfig;
    }

    @GetMapping("/completions.do")
    public void completions(String text) {
        OpenAiMessage openAiMessage = new OpenAiMessage(ChatGPTUtil.ROLE_USER, text);
        if (ObjectUtils.isEmpty(openAiRequest)) {
            openAiRequest = ChatGPTUtil.getDefalutRequest();
        }
        openAiRequest.getMessages().add(openAiMessage);
        OpenAiResponse openAiResponse = chatGPTUtil.completions(openAiRequest);
        OpenAiMessage replyMsg = openAiResponse.getChoices().getFirst().getMessage();
        openAiRequest.getMessages().add(replyMsg);
        System.out.println(replyMsg.content);

    }

    @SneakyThrows
    @GetMapping("/testVoiceAndSend.do")
    public void testVoiceAndSend() {
        String wavFileName = UUIDUtil.randomUUID() + LokTarConstant.VOICE_SUFFIX_WAV;
        azureVoiceUtil.textToWav(voicePath, wavFileName, "你叫什么名字");
        FFmpegUtil.convertWavToAmr(voicePath, wavFileName);
        testFileExist(voicePath,wavFileName);
        String filepath = voicePath + wavFileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR);
        UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(filepath), lokTarConfig.qywxAgent003Id);
        System.out.println(uploadMediaRsp.getMediaId());
        qywxApi.sendVoiceMsg(new AgentMsgVoice(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent003Id, uploadMediaRsp.getMediaId()));
    }

    @SneakyThrows
    private void testFileExist(String voicePath, String fileName) {
        System.out.println("fileName："+fileName);
        String coverFileName = fileName.lastIndexOf(LokTarConstant.VOICE_SUFFIX_WAV) != -1 ? fileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR) : fileName.replace(LokTarConstant.VOICE_SUFFIX_AMR, LokTarConstant.VOICE_SUFFIX_WAV);
        System.out.println(coverFileName);
        int times = 10;
        while (times > 0) {
            File file = new File(voicePath + coverFileName);
            if (file.exists()) {
                System.out.println("file exist "+ DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
                break;
            }
            System.out.println("file not exist "+DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
            times--;
            Thread.sleep(1000);
        }
    }


}
