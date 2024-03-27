package com.loktar.task.transmission;


import com.loktar.domain.transmission.TrRss;
import com.loktar.service.transmission.RssService;
import com.loktar.util.DelayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.List;

@Configuration
@EnableScheduling
public class RssTask {
    @Value("${spring.profiles.active}")
    private String env;

    private final RssService rssService;

    public RssTask(RssService rssService) {
        this.rssService = rssService;
    }


    @Scheduled(cron = "0 */1 * * * ?")
    private void refreshAndDealTrRssTorrents() {
        if (!env.equals("pro")) {
            return;
        }
//        System.out.println("Scheduled:refreshAndDealTrRssTorrents"+ DateUtil.format(new Date(),DateUtil.DATEFORMATSECOND));
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
//        System.out.println("minute:"+minute);
        List<TrRss> trRsss = rssService.getTrRsssByStatus(1);
        for (TrRss trRss : trRsss) {
            //System.out.println("minute%interval:"+minute % trRss.getIntervalMinutes());
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
