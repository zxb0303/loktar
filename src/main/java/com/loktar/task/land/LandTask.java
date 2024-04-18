package com.loktar.task.land;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.service.land.LandService;
import com.loktar.util.DateTimeUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@EnableScheduling
public class LandTask {
    private final LandService landService;

    private final LokTarConfig lokTarConfig;

    public LandTask(LandService landService, LokTarConfig lokTarConfig) {
        this.landService = landService;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 5 0 * * ?")
    private void updateLandData() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
            return;
        }
        System.out.println("土拍定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
        String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
        landService.updateLandData(year);
        System.out.println("土拍定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(),DateTimeUtil.FORMATTER_DATESECOND));
    }
}
