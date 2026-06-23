package com.loktar.domain.iyuu;

import lombok.Data;

import java.io.Serializable;

@Data
public class IyuuTorrent implements Serializable {
    private String infoHash;

    private Integer sid;

    private Integer torrentId;

    private static final long serialVersionUID = 1L;
}