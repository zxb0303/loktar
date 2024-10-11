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
import com.loktar.util.RedisUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
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
    private final RedisUtil redisUtil;

    public PatentTask(QywxPatentMsgMapper qywxPatentMsgMapper, PatentPdfApplyMapper patentPdfApplyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig, RedisUtil redisUtil) {
        this.qywxPatentMsgMapper = qywxPatentMsgMapper;
        this.patentPdfApplyMapper = patentPdfApplyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
        this.redisUtil = redisUtil;
    }

    @Scheduled(cron = "0 */3 7-22 * * *")
    public void patentMonitor() {
        StringBuilder replymsg = new StringBuilder();
        String status = (String) redisUtil.get(LokTarConstant.REDIS_KEY_PATENT_MONITOR_SWITCH);
        if (StringUtils.isEmpty(status)) {
            return;
        }
        Integer redisCount = (Integer) redisUtil.get(LokTarConstant.REDIS_KEY_PATENT_MONITOR_COUNT);
        int dbCount = patentPdfApplyMapper.getCountByStatus(0);
        if (dbCount == 0) {
            replymsg.append("专利查询已完成").append(System.lineSeparator());
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), replymsg.toString()));
            redisUtil.del(LokTarConstant.REDIS_KEY_PATENT_MONITOR_COUNT);
            return;
        }
        if (redisCount == null || redisCount.intValue() != dbCount) {
            redisUtil.set(LokTarConstant.REDIS_KEY_PATENT_MONITOR_COUNT, dbCount, -1);
            return;
        }
        replymsg.append("专利查询异常").append(System.lineSeparator());
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), replymsg.toString()));

    }

    @Scheduled(cron = "0 0 1,19 * * *")
    public void updatePatentPdfApply() {
        System.out.println("updatePatentPdfApply定时器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        patentPdfApplyMapper.updatePatentPdfApply();
    }


    @Scheduled(cron = "*/3 * * * * *")
    public void dealQywxPatentMsg() {
        boolean lock = redisUtil.setIfAbsent(LokTarConstant.REDIS_KEY_QYWX_PATENT_MSG_TASK_LOCK, "1", 10);
        if (lock) {
            return;
        }
        try {
            List<QywxPatentMsg> qywxPatentMsgs = qywxPatentMsgMapper.getQywxPatentMsgsByStatus(LokTarConstant.QYWX_PATENT_MSG_STATUS_CREATED);
            for (QywxPatentMsg qywxPatentMsg : qywxPatentMsgs) {
                List<File> files = new ArrayList<>();
                if (qywxPatentMsg.getType().equals(LokTarConstant.QYWX_PATENT_MSG_TYPE_QUOTATION)) {
                    File file = new File(lokTarConfig.getPath().getPatent() + MessageFormat.format(LokTarConstant.PATENT_QUOTATION_FILE_PATH, qywxPatentMsg.getApplyName()));
                    testFileExist(file);
                    files.add(file);
                    sendSmsMsg(qywxPatentMsg, file);
                }
                if (qywxPatentMsg.getType().equals(LokTarConstant.QYWX_PATENT_MSG_TYPE_CONTRACT)) {
                    File file1 = new File(lokTarConfig.getPath().getPatent() + MessageFormat.format(LokTarConstant.PATENT_CONTRACT_FILE_PATH, qywxPatentMsg.getApplyName()));
                    File file2 = new File(lokTarConfig.getPath().getPatent() + MessageFormat.format(LokTarConstant.PATENT_AGREEMENT_FILE_PATH, qywxPatentMsg.getApplyName()));
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
                qywxPatentMsgMapper.updateQywxPatentStatusById(qywxPatentMsg.getId(), LokTarConstant.QYWX_PATENT_MSG_STATUS_SENDED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisUtil.del(LokTarConstant.REDIS_KEY_QYWX_PATENT_MSG_TASK_LOCK);
        }
    }

    private void sendSmsMsg(QywxPatentMsg qywxPatentMsg, File file) {
        if (!qywxPatentMsg.getAgentId().equals(lokTarConfig.getQywx().getAgent007Id())) {
            return;
        }
        String msg = "";
        if (qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeCc())) {
            msg = PatentSmsUtil.getSmsMsgForCc(qywxPatentMsg, file);
        } else {
            String mobiles = qywxPatentMsgMapper.getMobileStrByApplyName(qywxPatentMsg.getApplyName());
            if (StringUtils.isEmpty(mobiles)) {
                mobiles = "暂无号码";
            }
            mobiles = mobiles.replace(";", ",");
            mobiles = mobiles.replace(",-", "");
            msg = PatentSmsUtil.getSmsMsgForCj(mobiles, qywxPatentMsg, file);
        }
        qywxApi.sendTextMsg(new AgentMsgText(qywxPatentMsg.getFromUserName(), qywxPatentMsg.getAgentId(), msg));
        if (!qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeZxb())) {
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), qywxPatentMsg.getAgentId(), msg));
        }
        if (!qywxPatentMsg.getFromUserName().equals(lokTarConfig.getQywx().getNoticeCxy())) {
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), qywxPatentMsg.getAgentId(), msg));
        }
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
