package com.loktar.task.patent;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.qywx.QywxPatentMsg;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgFile;
import com.loktar.mapper.qywx.QywxPatentMsgMapper;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class PatentTask {
    private final QywxPatentMsgMapper qywxPatentMsgMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;
    private boolean isProcessing = false;

    public PatentTask(QywxPatentMsgMapper qywxPatentMsgMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxPatentMsgMapper = qywxPatentMsgMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }


    @Scheduled(cron = "*/3 * * * * *")
    public void dealQywxPatentMsg() {
        System.out.println("dealQywxPatentMsg");
        if (isProcessing) {
            return;
        }
        isProcessing = true;
        List<QywxPatentMsg> qywxPatentMsgs = qywxPatentMsgMapper.getQywxPatentMsgsByStatus("01");

        for (QywxPatentMsg qywxPatentMsg : qywxPatentMsgs) {
            List<File> files = new ArrayList<>();
            if (qywxPatentMsg.getType().equals("01")) {
                File file = new File(lokTarConfig.getPath().getPatent() + "quotation/" + qywxPatentMsg.getApplyName() + ".xlsx");
                files.add(file);
            }
            if (qywxPatentMsg.getType().equals("02")) {
                File file1 = new File(lokTarConfig.getPath().getPatent() + "contract/收购合同-" + qywxPatentMsg.getApplyName() + ".doc");
                File file2 = new File(lokTarConfig.getPath().getPatent() + "contract/转让协议-" + qywxPatentMsg.getApplyName() + ".doc");
                files.add(file1);
                files.add(file2);
            }
            for (File file : files) {
                UploadMediaRsp uploadMediaRsp = qywxApi.uploadMediaForPatent(file, lokTarConfig.getQywx().getAgent006Id());
                qywxApi.sendFileMsg(new AgentMsgFile(qywxPatentMsg.getFromUserName(), lokTarConfig.getQywx().getAgent006Id(), uploadMediaRsp.getMediaId()));
            }
            qywxPatentMsgMapper.updateQywxPatentStatusById(qywxPatentMsg.getId(),"02");
        }
        isProcessing = false;
    }

}
