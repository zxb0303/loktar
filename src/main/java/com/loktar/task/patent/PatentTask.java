package com.loktar.task.patent;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.qywx.QywxPatentMsg;
import com.loktar.dto.wx.UploadMediaRsp;
import com.loktar.dto.wx.agentmsg.AgentMsgFile;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.patent.PatentPdfApplyMapper;
import com.loktar.mapper.qywx.QywxPatentMsgMapper;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.PatentSmsUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class PatentTask {
    private final QywxPatentMsgMapper qywxPatentMsgMapper;
    private final PatentPdfApplyMapper patentPdfApplyMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;
    private boolean isProcessing = false;

    public PatentTask(QywxPatentMsgMapper qywxPatentMsgMapper, PatentPdfApplyMapper patentPdfApplyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.qywxPatentMsgMapper = qywxPatentMsgMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 0 1,19 * * *")
    public void updatePatentPdfApply() {
        System.out.println("updatePatentPdfApply定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        patentPdfApplyMapper.updatePatentPdfApply();
    }


    @Scheduled(cron = "*/3 * * * * *")
    public void dealQywxPatentMsg() {
        if (isProcessing) {
            return;
        }
        isProcessing = true;
        try {
            List<QywxPatentMsg> qywxPatentMsgs = qywxPatentMsgMapper.getQywxPatentMsgsByStatus("01");
            for (QywxPatentMsg qywxPatentMsg : qywxPatentMsgs) {
                List<File> files = new ArrayList<>();
                if (qywxPatentMsg.getType().equals("01")) {
                    File file = new File(lokTarConfig.getPath().getPatent() + "quotation/" + qywxPatentMsg.getApplyName() + ".xlsx");
                    testFileExist(file);
                    files.add(file);
                    sendSmsMsg(qywxPatentMsg, file);
                }
                if (qywxPatentMsg.getType().equals("02")) {
                    File file1 = new File(lokTarConfig.getPath().getPatent() + "contract/收购合同-" + qywxPatentMsg.getApplyName() + ".doc");
                    File file2 = new File(lokTarConfig.getPath().getPatent() + "contract/转让协议-" + qywxPatentMsg.getApplyName() + ".doc");
                    testFileExist(file1);
                    testFileExist(file2);
                    files.add(file1);
                    files.add(file2);
                }
                for (File file : files) {
                    UploadMediaRsp uploadMediaRsp = qywxApi.uploadMediaForPatent(file, qywxPatentMsg.getAgentId());
                    qywxApi.sendFileMsg(new AgentMsgFile(qywxPatentMsg.getFromUserName(), qywxPatentMsg.getAgentId(), uploadMediaRsp.getMediaId()));
                    if (!qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeZxb())) {
                        qywxApi.sendFileMsg(new AgentMsgFile(lokTarConfig.getQywx().getNoticeZxb(), qywxPatentMsg.getAgentId(), uploadMediaRsp.getMediaId()));
                    }
                    if (!qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeCxy())) {
                        qywxApi.sendFileMsg(new AgentMsgFile(lokTarConfig.getQywx().getNoticeCxy(), qywxPatentMsg.getAgentId(), uploadMediaRsp.getMediaId()));
                    }
                }
                qywxPatentMsgMapper.updateQywxPatentStatusById(qywxPatentMsg.getId(), "02");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isProcessing = false;
        }
    }

    private void sendSmsMsg(QywxPatentMsg qywxPatentMsg, File file) {
        if(!qywxPatentMsg.getAgentId().equals(lokTarConfig.getQywx().getAgent007Id())){
            return ;
        }
        String mobiles = qywxPatentMsgMapper.getMobileStrByApplyName(qywxPatentMsg.getApplyName());
        mobiles = mobiles.replace(";", ",");
        mobiles = mobiles.replace(",-", "");
        String str = PatentSmsUtil.getSmsMsg(mobiles, qywxPatentMsg.getApplyName(), file);
        qywxApi.sendTextMsg(new AgentMsgText(qywxPatentMsg.getFromUserName(), qywxPatentMsg.getAgentId(), str));
    }

    @SneakyThrows
    private void testFileExist(File file) {
        int times = 20;
        while (times > 0) {
            if (file.exists()) {
                break;
            }
            times--;
            Thread.sleep(1000);
        }
    }
}
