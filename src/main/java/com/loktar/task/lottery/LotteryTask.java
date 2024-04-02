package com.loktar.task.lottery;

import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class LotteryTask {
    private final HZLotteryServiceV2 hZLotteryServiceV2;


    @Value("${spring.profiles.active}")
    private String env;

    public LotteryTask(HZLotteryServiceV2 hZLotteryServiceV2) {
        this.hZLotteryServiceV2 = hZLotteryServiceV2;
    }


    @Scheduled(cron = "0 30 9,10,11,12,13 * * ?")
    private void updateHZLotteryData() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("杭州摇号数据定时器开始：" + DateUtil.getTodayToSecond());
        hZLotteryServiceV2.updateHZLotteryData();
        System.out.println("杭州摇号数据定时器结束：" + DateUtil.getTodayToSecond());
    }


}
