package com.loktar.service.common.impl;

import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.lottery.LotteryHouse;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.lottery.LotteryHouseMapper;
import com.loktar.service.common.CommonService;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommonServiceImpl implements CommonService {

    private final LotteryHouseMapper lotteryHouseMapper;

    private final QywxApi qywxApi;

    public CommonServiceImpl(LotteryHouseMapper lotteryHouseMapper, QywxApi qywxApi) {
        this.lotteryHouseMapper = lotteryHouseMapper;
        this.qywxApi = qywxApi;
    }


    @Override
    public void sendLotteryNotice() {
        String content = LokTarConstant.NOTICE_TITLE_LOTTERY + "(" + DateUtil.getYestodayStr() + ")" + "\n\n";
        List<LotteryHouse> lotteryHouses = lotteryHouseMapper.getYesterdayLotteryHouses();
        if (lotteryHouses.size() == 0) {
            return;
        }
        for (int i = 0; i < lotteryHouses.size(); i++) {
            LotteryHouse lotteryHouse = lotteryHouses.get(i);
            String name = lotteryHouse.getHouseName();
            String area = "";
            int price = 0;
            double totalChance = lotteryHouse.getTotalHouseNum() * 1.0 / lotteryHouse.getTotalPeopleNum() * 1.0;
            String totalChanceStr = totalChance >= 1 ? "100%" : String.format("%.2f", totalChance * 100) + "%";
            content = content
                    + lotteryHouse.getHouseName()
                    + "\n"
                    + "房源:" + lotteryHouse.getTotalHouseNum() + ";登记:" + lotteryHouse.getTotalPeopleNum() + ";中签率:" + totalChanceStr
                    + "\n\n";
        }
        content = content + DateUtil.getMinuteSysDate();
        qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
    }

}
