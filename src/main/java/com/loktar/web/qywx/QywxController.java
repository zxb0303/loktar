package com.loktar.web.qywx;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.wx.AccessToken;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("wxapi")
public class QywxController {

    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    public QywxController(QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @GetMapping("/send.do")
    public void send() {
        String content1 = "试试1";
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent006Id(), content1));
        String content2 = "试试2";
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent007Id(), content2));
//        qywxApi.sendVoiceMsg(new AgentMsgVoice(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent003Id(), "3zUEeZmUuc-Eno-3qO9bDgClOpEoEL2XvqTyGpPpOqmrTswb-zG3rzsUOK8IKC5by"));
    //
//        qywxApi.sendFileMsg(new AgentMsgFile(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent006Id(), mediaId));
//        qywxApi.sendMarkdownMsg(new AgentMsgMarkdown(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent007Id(), "您的会议室已经预定，稍后会同步到`邮箱`  \\n>**事项详情**  \\n>事　项：<font color=\\\"info\\\">开会</font>  \\n>组织者：@miglioguan  \\n>参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang  \\n>  \\n>会议室：<font color=\\\"info\\\">广州TIT 1楼 301</font>  \\n>日　期：<font color=\\\"warning\\\">2018年5月18日</font>  \\n>时　间：<font color=\\\"comment\\\">上午9:00-11:00</font>  \\n>  \\n>请准时参加会议。  \\n>  \\n>如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)" ));
    }

    @GetMapping("/getAccessToken.do")
    public void getAccessToken(String agentId) {
        AccessToken accessToken = qywxApi.accessToken(agentId);
        System.out.println(accessToken.toString());
    }


    @SneakyThrows
    @GetMapping("/download.do")
    public void download() {
        String voicePath = "F:/voice/";
        String mediaId = "15nIsJJ02LJ4yc67RXJdBdwoMFAaNzNty8fVfT2f9TH-LB33FEPvCEw2kYFfetpq_";
        String agentId = lokTarConfig.getQywx().getAgent003Id();
        String filename = qywxApi.saveMedia(voicePath, mediaId, agentId);
        System.out.println(filename);
    }

    @GetMapping("/upload.do")
    public void upload() {
        String voicePath = "F:/loktar/patent/";
        String amrFilename = "a.xlsx";
        String agentId = lokTarConfig.getQywx().getAgent003Id();
        UploadMediaRsp uploadMediaRsp = qywxApi.uploadMedia(new File(voicePath + amrFilename), agentId);
        System.out.println(uploadMediaRsp.getMediaId());
    }
    @GetMapping("/upload2.do")
    public void upload2() {
        String voicePath = "F:/loktar/patent/";
        String amrFilename = "a.xlsx";
        String agentId = lokTarConfig.getQywx().getAgent003Id();
        UploadMediaRsp uploadMediaRsp = qywxApi.uploadMediaForPatent(new File(voicePath + amrFilename), agentId);
        System.out.println(uploadMediaRsp.getMediaId());
    }

}
