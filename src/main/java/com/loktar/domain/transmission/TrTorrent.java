package com.loktar.domain.transmission;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrTorrent implements Serializable {
    private Integer id;

    private String name;

    private Integer status;

    private Long totalSize;

    private String downloadDir;

    private Integer error;

    private String errorString;

    private String hashString;

    private Double uploadRatio;

    private Double percentDone;

    private Integer peersSendingToUs;

    private Integer peersGettingFromUs;

    private Integer rateUpload;

    private Integer addedDate;

    private Integer activityDate;

    private Integer doneDate;

    private static final long serialVersionUID = 1L;
}