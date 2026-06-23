package com.loktar.domain.transmission;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrTorrentTracker implements Serializable {
    private Integer id;

    private Integer torrentId;

    private String host;

    private Integer seederCount;

    private Integer leecherCount;

    private Integer announceState;

    private String announce;

    private static final long serialVersionUID = 1L;
}