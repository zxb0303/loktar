package com.loktar.task.cxy;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.cxy.Contract;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.cxy.ContractMapper;
import com.loktar.util.DateTimeUtil;
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
public class ContracTask {

    private final QywxApi qywxApi;

    private final ContractMapper contractMapper;

    private final LokTarConfig lokTarConfig;


    public ContracTask(QywxApi qywxApi, ContractMapper contractMapper, LokTarConfig lokTarConfig) {
        this.qywxApi = qywxApi;
        this.contractMapper = contractMapper;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 30 10,11,12 * * MON-FRI")
    private void notice() {
        System.out.println("合同到期监测器：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        List<Contract> contracts = contractMapper.getNeedNoticeContracts();
        if (ObjectUtils.isEmpty(contracts)) {
            return;
        }
        StringBuilder content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_CONTRACT).append(System.lineSeparator());
        for (Contract cntract : contracts) {
            content.append(System.lineSeparator())
                    .append(cntract.getParty()).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(cntract.getCounterParty())
                    .append(System.lineSeparator())
                    .append(cntract.getNumber()).append(System.lineSeparator())
                    .append(System.lineSeparator()).append(cntract.getEndDate()).append("到期").append(System.lineSeparator())
                    .append(System.lineSeparator());
            cntract.setStatus(1);
            contractMapper.updateByPrimaryKey(cntract);
        }
        content.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATEMINUTE));
        //qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeCxy(), lokTarConfig.getQywx().getAgent002Id(), content.toString()));
    }
}
