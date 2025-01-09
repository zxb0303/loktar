package com.loktar.web.transmission;

import com.loktar.conf.LokTarConfig;
import com.loktar.domain.transmission.TrRss;
import com.loktar.service.transmission.RssService;
import com.loktar.service.transmission.TransmissionService;
import com.loktar.util.DelayUtil;
import com.loktar.util.TransmissionUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("transmission")
public class TransmissionController {
    private final TransmissionService transmissionService;
    private final RssService rssService;

    private final TransmissionUtil transmissionUtil;
    private final LokTarConfig lokTarConfig;

    public TransmissionController(TransmissionService transmissionService, RssService rssService, TransmissionUtil transmissionUtil, LokTarConfig lokTarConfig) {
        this.transmissionService = transmissionService;
        this.rssService = rssService;
        this.transmissionUtil = transmissionUtil;
        this.lokTarConfig = lokTarConfig;
    }
    @GetMapping("/testRss.do")
    public void testRss() {
        List<TrRss> trRsss = rssService.getTrRsssByStatus(1);
        for (TrRss trRss : trRsss) {
            DelayUtil.delaySeconds(2, 5);
            //TODO 打印
            System.out.println("transmission:refreshAndDealTrRssTorrents:每隔" + trRss.getIntervalMinutes() + "分钟执行RSS:" + trRss.getHostCnName());
            rssService.refreshTrRssTorrents(trRss);
            rssService.dealTrRssTorrents(trRss);

        }
    }


    @GetMapping("/testAdd.do")
    public void testAdd() {
        String url="";
        String downloadDir="/downloads2/complete";
        boolean paused=false;
        transmissionUtil.addTorrent(url,downloadDir,paused);
    }

    @GetMapping("/start.do")
    public void testStart() {
        Integer[] ids= new Integer[]{2919};
        transmissionUtil.startTorrents(ids);
    }

    @GetMapping("/removeTorrents.do")
    public void testRemoveTorrents() {
        transmissionUtil.removeTorrents(new Integer[]{2486},true);
    }

    @GetMapping("/getFreeSpaceByPath.do")
    public void testGetFreeSpaceByPath() {
        System.out.println(transmissionUtil.getFreeSpaceByPath(lokTarConfig.getTransmission().getTempDownloadDir()).toString());
    }

    @GetMapping("/altSpeedEnabled.do")
    public void altSpeedEnabled() {
        transmissionUtil.altSpeedEnabled(false);
    }

    @GetMapping("/getSession.do")
    public void getSession() {
        transmissionUtil.getSession();
    }

    @GetMapping("/refreshAllTorrents.do")
    public void refreshAllTorrents() {
        transmissionService.refreshAllTorrents();
    }

    @GetMapping("/autoRemoveError.do")
    public void autoRemoveError() {
        transmissionService.autoRemoveError();
    }


}
