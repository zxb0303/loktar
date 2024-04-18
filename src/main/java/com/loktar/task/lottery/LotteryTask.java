package com.loktar.task.lottery;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class LotteryTask {
    private final HZLotteryServiceV2 hZLotteryServiceV2;
    private final LokTarConfig lokTarConfig;


    public LotteryTask(HZLotteryServiceV2 hZLotteryServiceV2, LokTarConfig lokTarConfig) {
        this.hZLotteryServiceV2 = hZLotteryServiceV2;
        this.lokTarConfig = lokTarConfig;
    }


    @Scheduled(cron = "0 30 9,10,11,12,13 * * ?")
    private void updateHZLotteryData() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        System.out.println("杭州摇号数据定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        hZLotteryServiceV2.updateHZLotteryData();
        System.out.println("杭州摇号数据定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
    }


}
