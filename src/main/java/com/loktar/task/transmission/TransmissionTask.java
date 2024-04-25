package com.loktar.task.transmission;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.service.transmission.TransmissionService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Profile(LokTarConstant.ENV_PRO)
public class TransmissionTask {
    private final TransmissionService transmissionService;

    private final LokTarConfig lokTarConfig;

    public TransmissionTask(TransmissionService transmissionService, LokTarConfig lokTarConfig) {
        this.transmissionService = transmissionService;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    private void refresh() {
        //TODO 打印
        System.out.println("transmission:refreshTorrents");
        transmissionService.refreshAllTorrents();
        transmissionService.autoStart();
        transmissionService.autoRemove(lokTarConfig.getTransmission().getMinSizeGB(), lokTarConfig.getTransmission().getDays(), lokTarConfig.getTransmission().getTempDownloadDir());
        transmissionService.autoRemoveError();

    }
}
