package com.loktar.domain.iyuu;

import java.io.Serializable;

public class IyuuTorrent implements Serializable {
    private String infoHash;

    private Integer sid;

    private Integer torrentId;

    private static final long serialVersionUID = 1L;

    public String getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(String infoHash) {
        this.infoHash = infoHash == null ? null : infoHash.trim();
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(Integer torrentId) {
        this.torrentId = torrentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", infoHash=").append(infoHash);
        sb.append(", sid=").append(sid);
        sb.append(", torrentId=").append(torrentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}