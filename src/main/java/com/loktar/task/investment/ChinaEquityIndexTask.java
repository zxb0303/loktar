package com.loktar.task.investment;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.investment.EquityIndexDividendYieldDaily;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.investment.EquityIndexDividendYieldDailyMapper;
import com.loktar.util.CHinaEquityIndexUtil;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class ChinaEquityIndexTask {
    private final EquityIndexDividendYieldDailyMapper equityIndexDividendYieldDailyMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    public ChinaEquityIndexTask(EquityIndexDividendYieldDailyMapper equityIndexDividendYieldDailyMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.equityIndexDividendYieldDailyMapper = equityIndexDividendYieldDailyMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 0 7 * * MON-FRI")
    private void getData() {
        for (String index : CHinaEquityIndexUtil.EQUITY_INDEXS) {
            String fileUrl = MessageFormat.format(CHinaEquityIndexUtil.INDICATOR_URL, index);
            List<EquityIndexDividendYieldDaily> result = CHinaEquityIndexUtil.readExcelFromUrl(fileUrl);
            for (EquityIndexDividendYieldDaily entity : result) {
                equityIndexDividendYieldDailyMapper.insertIgnore(entity);
            }
        }
    }

    @Scheduled(cron = "0 30 7 * * MON-FRI")
    public void sendMsg() {
        List<EquityIndexDividendYieldDaily> result = equityIndexDividendYieldDailyMapper.getRecentEquityIndexDividendYieldDaily();
        StringBuilder msg = new StringBuilder();
        msg.append("上一交易日红利指数股息率情况：").append(System.lineSeparator()).append(System.lineSeparator());
        for (EquityIndexDividendYieldDaily entity : result) {
            msg.append(entity.getEquityIndexName() + "(" + entity.getEquityIndex() + ")：" + entity.getDividendYield() + "%").append(System.lineSeparator());
        }
        msg.append(System.lineSeparator());
        msg.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE));
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent009Id(), msg.toString()));
        System.out.println(result);
    }

}
