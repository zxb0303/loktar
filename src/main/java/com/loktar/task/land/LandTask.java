package com.loktar.task.land;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.service.land.LandService;
import com.loktar.util.DateUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
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
        System.out.println("土拍定时器开始：" + DateUtil.getTodayToSecond());
        String year = DateUtil.format(new Date(), DateUtil.DATEFORMATYEAR);
        landService.updateLandData(year);
        System.out.println("土拍定时器结束：" + DateUtil.getTodayToSecond());
    }
}
