package com.loktar.domain.transmission;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrRss implements Serializable {
    private Integer rssId;

    private String hostCnName;

    private String rssUrl;

    private String pattern;

    private String commonDownloadUrl;

    private Integer status;

    private Integer intervalMinutes;

    private static final long serialVersionUID = 1L;
}