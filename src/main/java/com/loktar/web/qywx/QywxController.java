package com.loktar.web.qywx;

import com.loktar.conf.LokTarConfig;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgFile;
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
    public void send(String mediaId) {

//        String content = LokTarConstant.NOTICE_TITLE_GITHUB + "\n\n"
//                + "试试"
//                + "\n\n" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATEMINUTE);
//        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent006Id(), content));
//        qywxApi.sendVoiceMsg(new AgentMsgVoice(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent003Id(), "3zUEeZmUuc-Eno-3qO9bDgClOpEoEL2XvqTyGpPpOqmrTswb-zG3rzsUOK8IKC5by"));
    //
        qywxApi.sendFileMsg(new AgentMsgFile(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent006Id(), mediaId));




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
