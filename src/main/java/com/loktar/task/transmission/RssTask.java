package com.loktar.task.transmission;


import com.loktar.conf.LokTarConstant;
import com.loktar.domain.transmission.TrRss;
import com.loktar.service.transmission.RssService;
import com.loktar.util.DelayUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class RssTask {

    private final RssService rssService;

    public RssTask(RssService rssService) {
        this.rssService = rssService;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    private void refreshAndDealTrRssTorrents() {

        LocalTime now = LocalTime.now();
        int minute = now.getMinute();
        List<TrRss> trRsss = rssService.getTrRsssByStatus(1);
        for (TrRss trRss : trRsss) {
            if (minute % trRss.getIntervalMinutes() == 0) {
                DelayUtil.delaySeconds(2, 5);
                //TODO 打印
                System.out.println("transmission:refreshAndDealTrRssTorrents:每隔" + trRss.getIntervalMinutes() + "分钟执行RSS:" + trRss.getHostCnName());
                rssService.refreshTrRssTorrents(trRss);
                rssService.dealTrRssTorrents(trRss);
            }
        }
    }
}
