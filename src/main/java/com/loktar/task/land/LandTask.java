package com.loktar.task.land;



import lombok.extern.slf4j.Slf4j;
import com.loktar.service.land.LandService;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class LandTask {
    private final LandService landService;


    public LandTask(LandService landService) {
        this.landService = landService;
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void updateLandData() {
        log.info("{}", "土拍定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
        landService.updateLandData(year);
        log.info("{}", "土拍定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
    }
}
