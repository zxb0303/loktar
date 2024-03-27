package com.loktar.domain.transmission;

import java.io.Serializable;

public class TrRss implements Serializable {
    private Integer rssId;

    private String hostCnName;

    private String rssUrl;

    private String pattern;

    private String commonDownloadUrl;

    private Integer status;

    private Integer intervalMinutes;

    private static final long serialVersionUID = 1L;

    public Integer getRssId() {
        return rssId;
    }

    public void setRssId(Integer rssId) {
        this.rssId = rssId;
    }

    public String getHostCnName() {
        return hostCnName;
    }

    public void setHostCnName(String hostCnName) {
        this.hostCnName = hostCnName == null ? null : hostCnName.trim();
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public void setRssUrl(String rssUrl) {
        this.rssUrl = rssUrl == null ? null : rssUrl.trim();
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern == null ? null : pattern.trim();
    }

    public String getCommonDownloadUrl() {
        return commonDownloadUrl;
    }

    public void setCommonDownloadUrl(String commonDownloadUrl) {
        this.commonDownloadUrl = commonDownloadUrl == null ? null : commonDownloadUrl.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", rssId=").append(rssId);
        sb.append(", hostCnName=").append(hostCnName);
        sb.append(", rssUrl=").append(rssUrl);
        sb.append(", pattern=").append(pattern);
        sb.append(", commonDownloadUrl=").append(commonDownloadUrl);
        sb.append(", status=").append(status);
        sb.append(", intervalMinutes=").append(intervalMinutes);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}