package com.loktar.task.lottery;

import com.loktar.conf.LokTarConstant;
import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.DateTimeUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class LotteryTask {
    private final HZLotteryServiceV2 hZLotteryServiceV2;


    public LotteryTask(HZLotteryServiceV2 hZLotteryServiceV2) {
        this.hZLotteryServiceV2 = hZLotteryServiceV2;
    }


    @Scheduled(cron = "0 30 9,10,11,12,13 * * ?")
    private void updateHZLotteryData() {

        System.out.println("杭州摇号数据定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        hZLotteryServiceV2.updateHZLotteryData();
        System.out.println("杭州摇号数据定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
    }


}
