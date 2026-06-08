package com.loktar.task.investment;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.investment.EquityIndexDividendYieldDaily;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.investment.EquityIndexDividendYieldDailyMapper;
import com.loktar.util.ChinaEquityIndexUtil;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
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

    @Scheduled(cron = "0 0/10 18-23 * * MON-FRI")
    @SneakyThrows
    public void syncAndNotify() {
        boolean hasNewData = false;
        for (String index : ChinaEquityIndexUtil.EQUITY_INDEXS) {
            String fileUrl = MessageFormat.format(ChinaEquityIndexUtil.INDICATOR_URL, index);
            List<EquityIndexDividendYieldDaily> result = ChinaEquityIndexUtil.readExcelFromUrl(fileUrl);
            if (result.isEmpty()) {
                System.out.println(index + " 未获取到数据");
                continue;
            }
            // Excel中按日期倒序排列，取最新一条
            EquityIndexDividendYieldDaily latest = result.get(result.size() - 1);
            EquityIndexDividendYieldDaily exist = equityIndexDividendYieldDailyMapper.selectByEquityIndexAndDate(latest.getEquityIndex(), latest.getDate());
            if (exist != null) {
                System.out.println(index + " 最新数据已存在(" + latest.getDate() + ")，跳过");
                continue;
            }
            latest.setCreateTime(LocalDateTime.now());
            latest.setUpdateTime(LocalDateTime.now());
            equityIndexDividendYieldDailyMapper.insertIgnore(latest);
            hasNewData = true;
            System.out.println(index + " 新增成功：" + latest.getDate());
        }

        if (hasNewData) {
            // 获取每个指数的最新数据，判断是否所有指数都已更新到同一日期
            List<EquityIndexDividendYieldDaily> recentList = equityIndexDividendYieldDailyMapper.getRecentEquityIndexDividendYieldDaily();
            if (recentList.size() == ChinaEquityIndexUtil.EQUITY_INDEXS.size()) {
                String latestDate = recentList.get(0).getDate();
                boolean allSameDate = recentList.stream().allMatch(e -> latestDate.equals(e.getDate()));
                if (allSameDate) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("红利指数股息率情况：").append(System.lineSeparator()).append(System.lineSeparator());
                    for (EquityIndexDividendYieldDaily entity : recentList) {
                        msg.append(entity.getEquityIndexName() + "(" + entity.getEquityIndex() + ")：" + entity.getDividendYield() + "%").append(System.lineSeparator());
                    }
                    msg.append(System.lineSeparator());
                    msg.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE));
                    qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent009Id(), msg.toString()));
                }
            }
        }
    }
}
