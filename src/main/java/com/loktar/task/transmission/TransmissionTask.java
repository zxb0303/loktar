package com.loktar.task.transmission;



import com.loktar.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import com.loktar.conf.LokTarConfig;
import com.loktar.service.transmission.TransmissionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TransmissionTask {
    private final TransmissionService transmissionService;

    private final LokTarConfig lokTarConfig;

    public TransmissionTask(TransmissionService transmissionService, LokTarConfig lokTarConfig) {
        this.transmissionService = transmissionService;
        this.lokTarConfig = lokTarConfig;
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void refresh() {
        //TODO 打印
        log.info("{}", "Transmission定时器："+ DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        transmissionService.refreshAllTorrents();
        transmissionService.autoStart();
        transmissionService.autoRemove(lokTarConfig.getTransmission().getMinSizeGB(), lokTarConfig.getTransmission().getDays(), lokTarConfig.getTransmission().getTempDownloadDir());
        transmissionService.autoRemoveError();

    }
}
