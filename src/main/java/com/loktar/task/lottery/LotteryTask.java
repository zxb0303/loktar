package com.loktar.task.lottery;


import lombok.extern.slf4j.Slf4j;
import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class LotteryTask {
    private final HZLotteryServiceV2 hZLotteryServiceV2;


    public LotteryTask(HZLotteryServiceV2 hZLotteryServiceV2) {
        this.hZLotteryServiceV2 = hZLotteryServiceV2;
    }


    @Scheduled(cron = "0 30 9 * * ?")
    public void updateHZLotteryData() {

        log.info("{}", "杭州摇号数据定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        hZLotteryServiceV2.updateHZLotteryData();
        log.info("{}", "杭州摇号数据定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
    }


}
