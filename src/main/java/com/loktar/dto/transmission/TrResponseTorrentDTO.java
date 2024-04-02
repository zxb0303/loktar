package com.loktar.dto.transmission;

import com.loktar.domain.transmission.TrTorrentTracker;
import lombok.Data;

import java.util.List;

@Data
public class TrResponseTorrentDTO {
    private long id;
    private String name;
    private int status;
    private long totalSize;
    private String downloadDir;
    private String hashString;
    private double uploadRatio;
    private double percentDone;
    private long peersSendingToUs;
    private long peersGettingFromUs;
    private long rateUpload;
    private int addedDate;
    private int activityDate;
    private int doneDate;
    private long error;
    private String errorString;
    private List<TrTorrentTracker> trackerStats;

}
