package com.loktar.web.transmission;

import com.loktar.conf.LokTarConfig;
import com.loktar.service.transmission.TransmissionService;
import com.loktar.util.TransmissionUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transmission")
public class TransmissionController {
    private final TransmissionService transmissionService;
    private final TransmissionUtil transmissionUtil;
    private final LokTarConfig lokTarConfig;

    public TransmissionController(TransmissionService transmissionService, TransmissionUtil transmissionUtil, LokTarConfig lokTarConfig) {
        this.transmissionService = transmissionService;
        this.transmissionUtil = transmissionUtil;
        this.lokTarConfig = lokTarConfig;
    }

    @RequestMapping("/testAdd.do")
    public void testAdd() {
        String url="";
        String downloadDir="/downloads2/complete";
        boolean paused=false;
        transmissionUtil.addTorrent(url,downloadDir,paused);
    }

    @RequestMapping("/testStart.do")
    public void testStart() {
        Integer[] ids= new Integer[]{2919};
        transmissionUtil.startTorrents(ids);
    }

    @RequestMapping("/testRemoveTorrents.do")
    public void testRemoveTorrents() {
        transmissionUtil.removeTorrents(new Integer[]{2486},true);
    }

    @RequestMapping("/testGetFreeSpaceByPath.do")
    public void testGetFreeSpaceByPath() {
        System.out.println(transmissionUtil.getFreeSpaceByPath(lokTarConfig.transmissionTempDownloadDir).toString());
    }

    @RequestMapping("/altSpeedEnabled.do")
    public void altSpeedEnabled() {
        transmissionUtil.altSpeedEnabled(false);
    }

    @RequestMapping("/getSession.do")
    public void getSession() {
        transmissionUtil.getSession();
    }

    @RequestMapping("/refreshAllTorrents.do")
    public void refreshAllTorrents() {
        transmissionService.refreshAllTorrents();
    }

    @RequestMapping("/autoRemoveError.do")
    public void autoRemoveError() {
        transmissionService.autoRemoveError();
    }


}
