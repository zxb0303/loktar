package com.loktar.web.openai;

import com.loktar.conf.LokTarPrivateConstant;
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

    @Value("${conf.voice.path}")
    private String voicePath;

    @Value("${conf.ffmpeg.path}")
    private String ffmpegPath;

    public ChatGPTController(QywxApi qywxApi) {
        this.qywxApi = qywxApi;
    }

    @RequestMapping("/completions.do")
    public void completions(String text) {
        OpenAiMessage openAiMessage = new OpenAiMessage(ChatGPTUtil.ROLE_USER, text);
        if (ObjectUtils.isEmpty(openAiRequest)) {
            openAiRequest = ChatGPTUtil.getDefalutRequest();
        }
        openAiRequest.getMessages().add(openAiMessage);
        OpenAiResponse openAiResponse = ChatGPTUtil.completions(openAiRequest);
        OpenAiMessage replyMsg = openAiResponse.getChoices().get(0).getMessage();
        openAiRequest.getMessages().add(replyMsg);
        System.out.println(replyMsg.content);

    }

    @SneakyThrows
    @RequestMapping("/testVoiceAndSend.do")
    public void testVoiceAndSend() {
        String fileName = UUID.randomUUID().toString();
        AzureUtil.textToWav(fileName, "你叫什么名字", voicePath,null);
        FFmpegUtil.convertWavToAmr(ffmpegPath, voicePath + fileName + AzureUtil.SUFFIX_WAV, voicePath + fileName + AzureUtil.SUFFIX_AMR);
        UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(voicePath + fileName + AzureUtil.SUFFIX_AMR),"voice",LokTarPrivateConstant.AGENT003ID);
        qywxApi.sendVoiceMsg(new AgentMsgVoice(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT003ID, uploadMediaRsp.getMediaId()));
    }

    @SneakyThrows
    @RequestMapping("/download.do")
    public void download() {
        String voicePath = "F:/voice/";
        String filename = "111";
        String mediaId = "3zUEeZmUuc-Eno-3qO9bDgClOpEoEL2XvqTyGpPpOqmrTswb-zG3rzsUOK8IKC5by";
        String agentId = LokTarPrivateConstant.AGENT003ID;
        qywxApi.getMediaAndSave(voicePath, filename, AzureUtil.SUFFIX_AMR, mediaId, agentId);
    }

}
