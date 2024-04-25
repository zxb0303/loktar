package com.loktar.service.common.impl;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.lottery.LotteryHouse;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.lottery.LotteryHouseMapper;
import com.loktar.service.common.CommonService;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class CommonServiceImpl implements CommonService {

    private final LotteryHouseMapper lotteryHouseMapper;

    private final QywxApi qywxApi;

    private final LokTarConfig lokTarConfig;

    public CommonServiceImpl(LotteryHouseMapper lotteryHouseMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.lotteryHouseMapper = lotteryHouseMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }


    @Override
    public void sendLotteryNotice() {
        String yestodayStr = DateTimeUtil.getDatetimeStr(LocalDateTime.now().minusDays(1), DateTimeUtil.FORMATTER_DATE);
        StringBuilder content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_LOTTERY + "(").append(yestodayStr).append(")").append(System.lineSeparator());
        List<LotteryHouse> lotteryHouses = lotteryHouseMapper.getYesterdayLotteryHouses();
        if (lotteryHouses.isEmpty()) {
            return;
        }
        for (LotteryHouse lotteryHouse : lotteryHouses) {
            double totalChance = lotteryHouse.getTotalHouseNum() * 1.0 / lotteryHouse.getTotalPeopleNum();
            String totalChanceStr = totalChance >= 1 ? "100%" : String.format("%.2f", totalChance * 100) + "%";
            content.append(System.lineSeparator())
                    .append(lotteryHouse.getHouseName()).append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("房源:").append(lotteryHouse.getTotalHouseNum()).append(";登记:").append(lotteryHouse.getTotalPeopleNum()).append(";中签率:").append(totalChanceStr).append(System.lineSeparator())
                    .append(System.lineSeparator());

        }
        content.append(DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATEMINUTE));
        qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content.toString()));
    }

}
