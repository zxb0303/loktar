package com.loktar.domain.iyuu;

import java.io.Serializable;

public class IyuuSite implements Serializable {
    private Integer id;

    private String site;

    private String nickname;

    private String baseUrl;

    private String downloadPage;

    private String reseedCheck;

    private Integer isHttps;

    private String supported;

    private Integer uid;

    private String passkey;

    private String downloadHash;

    private String trackHost;

    private Integer registered;

    private Integer importantLevel;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl == null ? null : baseUrl.trim();
    }

    public String getDownloadPage() {
        return downloadPage;
    }

    public void setDownloadPage(String downloadPage) {
        this.downloadPage = downloadPage == null ? null : downloadPage.trim();
    }

    public String getReseedCheck() {
        return reseedCheck;
    }

    public void setReseedCheck(String reseedCheck) {
        this.reseedCheck = reseedCheck == null ? null : reseedCheck.trim();
    }

    public Integer getIsHttps() {
        return isHttps;
    }

    public void setIsHttps(Integer isHttps) {
        this.isHttps = isHttps;
    }

    public String getSupported() {
        return supported;
    }

    public void setSupported(String supported) {
        this.supported = supported == null ? null : supported.trim();
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey == null ? null : passkey.trim();
    }

    public String getDownloadHash() {
        return downloadHash;
    }

    public void setDownloadHash(String downloadHash) {
        this.downloadHash = downloadHash == null ? null : downloadHash.trim();
    }

    public String getTrackHost() {
        return trackHost;
    }

    public void setTrackHost(String trackHost) {
        this.trackHost = trackHost == null ? null : trackHost.trim();
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }

    public Integer getImportantLevel() {
        return importantLevel;
    }

    public void setImportantLevel(Integer importantLevel) {
        this.importantLevel = importantLevel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", site=").append(site);
        sb.append(", nickname=").append(nickname);
        sb.append(", baseUrl=").append(baseUrl);
        sb.append(", downloadPage=").append(downloadPage);
        sb.append(", reseedCheck=").append(reseedCheck);
        sb.append(", isHttps=").append(isHttps);
        sb.append(", supported=").append(supported);
        sb.append(", uid=").append(uid);
        sb.append(", passkey=").append(passkey);
        sb.append(", downloadHash=").append(downloadHash);
        sb.append(", trackHost=").append(trackHost);
        sb.append(", registered=").append(registered);
        sb.append(", importantLevel=").append(importantLevel);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}