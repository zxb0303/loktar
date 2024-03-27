package com.loktar.service.transmission;


import com.loktar.dto.transmission.TrResponseDTO;

public interface TransmissionService {
    TrResponseDTO getFreeSpaceByDownloadDir(String downloadDir);

    TrResponseDTO refreshAllTorrents();

    void autoRemove(Long minSizeGG,int days,String downloadDir);

    void autoStart();
}
