package com.loktar.task.land;


import com.loktar.service.land.LandService;
import com.loktar.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class LandTask {
    private final LandService landService;

    @Value("${spring.profiles.active}")
    private String env;

    public LandTask(LandService landService) {
        this.landService = landService;
    }

    @Scheduled(cron = "0 5 0 * * ?")
    private void updateLandData() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("土拍定时器开始：" + DateUtil.getTodayToSecond());
        String year = DateUtil.format(new Date(), DateUtil.DATEFORMATYEAR);
        landService.updateLandData(year);
        System.out.println("土拍定时器结束：" + DateUtil.getTodayToSecond());
    }
}
