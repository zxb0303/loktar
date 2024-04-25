package com.loktar.web.transmission;

import com.loktar.conf.LokTarConfig;
import com.loktar.service.transmission.TransmissionService;
import com.loktar.util.TransmissionUtil;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/testAdd.do")
    public void testAdd() {
        String url="";
        String downloadDir="/downloads2/complete";
        boolean paused=false;
        transmissionUtil.addTorrent(url,downloadDir,paused);
    }

    @GetMapping("/testStart.do")
    public void testStart() {
        Integer[] ids= new Integer[]{2919};
        transmissionUtil.startTorrents(ids);
    }

    @GetMapping("/testRemoveTorrents.do")
    public void testRemoveTorrents() {
        transmissionUtil.removeTorrents(new Integer[]{2486},true);
    }

    @GetMapping("/testGetFreeSpaceByPath.do")
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
