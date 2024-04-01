package com.loktar.service.transmission;


import com.loktar.dto.transmission.TrResponse;

public interface TransmissionService {
    TrResponse getFreeSpaceByDownloadDir(String downloadDir);

    TrResponse refreshAllTorrents();

    void autoRemove(Long minSizeGG,int days,String downloadDir);

    void autoStart();
}
