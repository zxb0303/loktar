package com.loktar.domain.transmission;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TrRssTorrent implements Serializable {
    private Integer rssTorrentId;

    private Integer rssId;

    private String link;

    private Integer status;

    private String downloadUrl;

    private LocalDateTime pubDate;

    private String title;

    private static final long serialVersionUID = 1L;
}