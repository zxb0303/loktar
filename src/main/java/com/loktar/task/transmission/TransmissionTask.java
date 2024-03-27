package com.loktar.task.transmission;


import com.loktar.mapper.transmission.TrTorrentMapper;
import com.loktar.mapper.transmission.TrTorrentTrackerMapper;
import com.loktar.service.transmission.TransmissionService;
import com.loktar.util.DateUtil;
import com.loktar.util.TransmissionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class TransmissionTask {
    private final TransmissionService transmissionService;

    private final TrTorrentTrackerMapper trTorrentTrackerMapper;

    private final TrTorrentMapper trTorrentMapper;

    @Value("${spring.profiles.active}")
    private String env;

    private boolean refreshFlag = false;

    private boolean needTruncateFlag = true;

    private boolean autoRemoveFlag = false;

    public static Long minSizeGB = 900l;

    public static int DAYS = 15;

    public TransmissionTask(TransmissionService transmissionService, TrTorrentTrackerMapper trTorrentTrackerMapper, TrTorrentMapper trTorrentMapper) {
        this.transmissionService = transmissionService;
        this.trTorrentTrackerMapper = trTorrentTrackerMapper;
        this.trTorrentMapper = trTorrentMapper;
    }

    @Scheduled(cron = "0 0,20,40 * * * ?")
    private void autoStartAndRemove() {
        if (!env.equals("pro")) {
            return;
        }
//        System.out.println("Scheduled:autoRemove"+ DateUtil.format(new Date(),DateUtil.DATEFORMATSECOND));
        if (refreshFlag) {
            System.out.println("transmission:pass autoStartAndRemove");
            return;
        }
        if (needTruncateFlag) {
            System.out.println("重启或每天凌晨需刷新所有种子");
            trTorrentTrackerMapper.truncate();
            trTorrentMapper.truncate();
            needTruncateFlag = false;
            refreshAllTorrents();
            return;
        }
        System.out.println("transmission:autoStart");
        transmissionService.autoStart();
        System.out.println("transmission:autoRemove");
        autoRemoveFlag = true;
        transmissionService.autoRemove(minSizeGB,DAYS, TransmissionUtil.TEMP_DOWNLOAD_DIR);
        autoRemoveFlag = false;
    }

    @Scheduled(cron = "0 10,30,50 * * * ?")
    private void refreshAllTorrents() {
        if (!env.equals("pro")) {
            return;
        }
        if (autoRemoveFlag || needTruncateFlag || refreshFlag) {
            System.out.println("transmission:pass refreshAllTorrents and autoStart");
            return;
        }
        refreshFlag = true;
        //TODO 打印
        System.out.println("transmission:refreshAllTorrents");
        transmissionService.refreshAllTorrents();
        refreshFlag = false;

    }

    //每天的0点55分清空一下表
    @Scheduled(cron = "0 55 0 * * ?")
    private void truncateAllTorrents() {
        if (!env.equals("pro")) {
            return;
        }
        System.out.println("Scheduled:truncateAllTorrents"+ DateUtil.format(new Date(), DateUtil.DATEFORMATSECOND));
        needTruncateFlag = true;
    }
}
