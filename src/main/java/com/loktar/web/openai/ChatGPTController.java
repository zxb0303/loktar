package com.loktar.web.openai;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.openai.OpenAiMessage;
import com.loktar.dto.openai.OpenAiRequest;
import com.loktar.dto.openai.OpenAiResponse;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgVoice;
import com.loktar.util.AzureUtil;
import com.loktar.util.ChatGPTUtil;
import com.loktar.util.FFmpegUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("chatgpt")
public class ChatGPTController {
    private OpenAiRequest openAiRequest;

    private final QywxApi qywxApi;

    private final AzureUtil azureUtil;

    private final ChatGPTUtil chatGPTUtil;

    private final LokTarConfig lokTarConfig;

    @Value("${conf.voice.path}")
    private String voicePath;

    public ChatGPTController(QywxApi qywxApi, AzureUtil azureUtil, ChatGPTUtil chatGPTUtil, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.azureUtil = azureUtil;
        this.chatGPTUtil = chatGPTUtil;
        this.lokTarConfig = lokTarConfig;
    }

    @RequestMapping("/completions.do")
    public void completions(String text) {
        OpenAiMessage openAiMessage = new OpenAiMessage(ChatGPTUtil.ROLE_USER, text);
        if (ObjectUtils.isEmpty(openAiRequest)) {
            openAiRequest = ChatGPTUtil.getDefalutRequest();
        }
        openAiRequest.getMessages().add(openAiMessage);
        OpenAiResponse openAiResponse = chatGPTUtil.completions(openAiRequest);
        OpenAiMessage replyMsg = openAiResponse.getChoices().get(0).getMessage();
        openAiRequest.getMessages().add(replyMsg);
        System.out.println(replyMsg.content);

    }

    @SneakyThrows
    @RequestMapping("/testVoiceAndSend.do")
    public void testVoiceAndSend() {
        String wavFileName = UUID.randomUUID().toString() + LokTarConstant.VOICE_SUFFIX_WAV;
        azureUtil.textToWav(voicePath, wavFileName, "你叫什么名字");
        FFmpegUtil.convertWavToAmr(voicePath, wavFileName);
        String filepath = voicePath + wavFileName.replace(LokTarConstant.VOICE_SUFFIX_WAV, LokTarConstant.VOICE_SUFFIX_AMR);
        UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(filepath), lokTarConfig.qywxAgent003Id);
        System.out.println(uploadMediaRsp.getMediaId());
        qywxApi.sendVoiceMsg(new AgentMsgVoice(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent003Id, uploadMediaRsp.getMediaId()));
    }



}
