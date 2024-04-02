package com.loktar.task.cxy;


import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.cxy.Contract;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.cxy.ContractMapper;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class ContracTask {
    @Value("${spring.profiles.active}")
    private String env;

    private final QywxApi qywxApi;

    private final ContractMapper contractMapper;

    public ContracTask(QywxApi qywxApi, ContractMapper contractMapper) {
        this.qywxApi = qywxApi;
        this.contractMapper = contractMapper;
    }

    @Scheduled(cron = "0 30 10,11,12 * * MON-FRI")
    private void notice() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("合同到期监测器：" + DateUtil.getTodayToSecond());

        List<Contract> contracts = contractMapper.getNeedNoticeContracts();
        if (ObjectUtils.isEmpty(contracts)) {
            return;
        }
        String content = LokTarConstant.NOTICE_TITLE_CONTRAC + "\n\n";
        for (int i = 0; i < contracts.size(); i++) {
            Contract cntract = contracts.get(i);
            content = content + cntract.getParty()
                    + "\n"
                    + cntract.getCounterParty()
                    + "\n"
                    + cntract.getNumber()
                    + "\n"
                    + cntract.getEndDate() + "到期"
                    + "\n\n";
            cntract.setStatus(1);
            contractMapper.updateByPrimaryKey(cntract);
        }
        content = content + DateUtil.getMinuteSysDate();
        //qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
        qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_CXY, LokTarPrivateConstant.AGENT002ID, content));
    }
}
