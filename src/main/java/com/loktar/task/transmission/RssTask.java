package com.loktar.task.transmission;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.transmission.TrRss;
import com.loktar.service.transmission.RssService;
import com.loktar.util.DelayUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
@Component
@EnableScheduling
public class RssTask {

    private final RssService rssService;
    private final LokTarConfig lokTarConfig;

    public RssTask(RssService rssService, LokTarConfig lokTarConfig) {
        this.rssService = rssService;
        this.lokTarConfig = lokTarConfig;
    }


    @Scheduled(cron = "0 */1 * * * ?")
    private void refreshAndDealTrRssTorrents() {
        if (!lokTarConfig.env.equals(LokTarConstant.ENV_PRO)) {
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
