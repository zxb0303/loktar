package com.loktar.task.cxy;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.cxy.Contract;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.cxy.ContractMapper;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
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
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        System.out.println("合同到期监测器：" + DateUtil.getTodayToSecond());

        List<Contract> contracts = contractMapper.getNeedNoticeContracts();
        if (ObjectUtils.isEmpty(contracts)) {
            return;
        }
        StringBuilder content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_CONTRAC).append(System.lineSeparator());
        for (int i = 0; i < contracts.size(); i++) {
            Contract cntract = contracts.get(i);
            content.append(System.lineSeparator())
                    .append(cntract.getParty()).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(cntract.getCounterParty())
                    .append(System.lineSeparator())
                    .append(cntract.getNumber()).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append(cntract.getEndDate() + "到期").append(System.lineSeparator())
                    .append(System.lineSeparator());
            cntract.setStatus(1);
            contractMapper.updateByPrimaryKey(cntract);
        }
        content = content.append(DateUtil.getMinuteSysDate());
        //qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeZxb, lokTarConfig.qywxAgent002Id, content));
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.qywxNoticeCxy, lokTarConfig.qywxAgent002Id, content.toString()));
    }
}
