package com.loktar.domain.transmission;

import java.io.Serializable;

public class TrTorrentTracker implements Serializable {
    private Integer id;

    private Integer torrentId;

    private String host;

    private Integer seederCount;

    private Integer leecherCount;

    private Integer announceState;

    private String announce;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(Integer torrentId) {
        this.torrentId = torrentId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host == null ? null : host.trim();
    }

    public Integer getSeederCount() {
        return seederCount;
    }

    public void setSeederCount(Integer seederCount) {
        this.seederCount = seederCount;
    }

    public Integer getLeecherCount() {
        return leecherCount;
    }

    public void setLeecherCount(Integer leecherCount) {
        this.leecherCount = leecherCount;
    }

    public Integer getAnnounceState() {
        return announceState;
    }

    public void setAnnounceState(Integer announceState) {
        this.announceState = announceState;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce == null ? null : announce.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", torrentId=").append(torrentId);
        sb.append(", host=").append(host);
        sb.append(", seederCount=").append(seederCount);
        sb.append(", leecherCount=").append(leecherCount);
        sb.append(", announceState=").append(announceState);
        sb.append(", announce=").append(announce);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}