package com.loktar.task.patent;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.patent.PatentApply;
import com.loktar.domain.patent.PatentApplyDetail;
import com.loktar.domain.patent.PatentContent;
import com.loktar.domain.patent.PatentDetail;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.patent.PatentApplyDetailMapper;
import com.loktar.mapper.patent.PatentApplyMapper;
import com.loktar.mapper.patent.PatentContentMapper;
import com.loktar.mapper.patent.PatentDetailMapper;
import com.loktar.util.*;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class PatentTask {
    private final PatentDetailMapper patentDetailMapper;
    private final PatentApplyMapper patentApplyMapper;
    private final PatentApplyDetailMapper patentApplyDetailMapper;
    private final PatentContentMapper patentContentMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;
    private boolean isProcessing = false;

    public PatentTask(PatentDetailMapper patentDetailMapper, PatentApplyMapper patentApplyMapper, PatentApplyDetailMapper patentApplyDetailMapper, PatentContentMapper patentContentMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.patentDetailMapper = patentDetailMapper;
        this.patentApplyMapper = patentApplyMapper;
        this.patentApplyDetailMapper = patentApplyDetailMapper;
        this.patentContentMapper = patentContentMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }


    @Scheduled(cron = "0 */5 * * * ?")
    public void dealPatent() {
        if (isProcessing) {
            String content = LokTarConstant.NOTICE_PATENT_PROCESSIONG + System.lineSeparator()
                    + System.lineSeparator()
                    + "定时器时间太短处理不过来了" + System.lineSeparator()
                    + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
            qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent003Id(), content));
            return;
        }
        isProcessing = true;
        dealContent();
        dealDetail();
        isProcessing = false;
    }

    private void dealContent() {
        List<PatentContent> patentContents = patentContentMapper.selectByStatus(1);
        for (PatentContent patentContent : patentContents) {
            PatentDetail patentDetail = PatentUtil.dealPatentContent(patentContent.getContent());
            patentDetail.setPatentId(patentContent.getPatentId());
            patentContent.setStatus(2);
            patentContentMapper.updateByPrimaryKey(patentContent);
            if(ObjectUtils.isEmpty(patentDetailMapper.selectByPrimaryKey(patentContent.getPatentId()))){
                patentDetailMapper.insert(patentDetail);
            }else{
                patentDetailMapper.updateByPrimaryKey(patentDetail);
            }
        }
    }

    private void dealDetail() {
        List<PatentDetail> patentDetails = patentDetailMapper.selectByStatus(0);
        for (PatentDetail patentDetail : patentDetails) {
            String applyNameStr = patentDetail.getApplyName();
            if ("--".equals(applyNameStr) || "信息不完整或未找到".equals(applyNameStr)) {
                patentDetail.setStatus(1);
                patentDetailMapper.updateByPrimaryKey(patentDetail);
                continue;
            }
            String[] applyNames = new String[]{applyNameStr};
            if (applyNameStr.contains(",")) {
                applyNames = applyNameStr.split(",");
            }
            for (String applyName : applyNames) {
                PatentApply patentApply = patentApplyMapper.selectByApplyName(applyName);
                if (ObjectUtils.isEmpty(patentApply)) {
                    patentApply = new PatentApply();
                    patentApply.setApplyId(UUIDUtil.randomUUID());
                    patentApply.setApplyName(applyName);
                    patentApply.setStatus(0);
                    patentApplyMapper.insert(patentApply);
                }
                PatentApplyDetail patentApplyDetail = new PatentApplyDetail();
                patentApplyDetail.setApplyDetailId(UUIDUtil.randomUUID());
                patentApplyDetail.setPatentId(patentDetail.getPatentId());
                patentApplyDetail.setApplyId(patentApply.getApplyId());
                patentApplyDetailMapper.insert(patentApplyDetail);
            }
            patentDetail.setStatus(1);
            patentDetailMapper.updateByPrimaryKey(patentDetail);
        }
    }
}
