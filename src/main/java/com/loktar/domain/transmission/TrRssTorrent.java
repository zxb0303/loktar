package com.loktar.domain.transmission;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TrRssTorrent implements Serializable {
    private Integer rssTorrentId;

    private Integer rssId;

    private String link;

    private Integer status;

    private String downloadUrl;

    private LocalDateTime pubDate;

    private String title;

    private static final long serialVersionUID = 1L;

    public Integer getRssTorrentId() {
        return rssTorrentId;
    }

    public void setRssTorrentId(Integer rssTorrentId) {
        this.rssTorrentId = rssTorrentId;
    }

    public Integer getRssId() {
        return rssId;
    }

    public void setRssId(Integer rssId) {
        this.rssId = rssId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link == null ? null : link.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl == null ? null : downloadUrl.trim();
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", rssTorrentId=").append(rssTorrentId);
        sb.append(", rssId=").append(rssId);
        sb.append(", link=").append(link);
        sb.append(", status=").append(status);
        sb.append(", downloadUrl=").append(downloadUrl);
        sb.append(", pubDate=").append(pubDate);
        sb.append(", title=").append(title);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}