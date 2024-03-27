package com.loktar.domain.transmission;

import java.io.Serializable;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir == null ? null : downloadDir.trim();
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString == null ? null : errorString.trim();
    }

    public String getHashString() {
        return hashString;
    }

    public void setHashString(String hashString) {
        this.hashString = hashString == null ? null : hashString.trim();
    }

    public Double getUploadRatio() {
        return uploadRatio;
    }

    public void setUploadRatio(Double uploadRatio) {
        this.uploadRatio = uploadRatio;
    }

    public Double getPercentDone() {
        return percentDone;
    }

    public void setPercentDone(Double percentDone) {
        this.percentDone = percentDone;
    }

    public Integer getPeersSendingToUs() {
        return peersSendingToUs;
    }

    public void setPeersSendingToUs(Integer peersSendingToUs) {
        this.peersSendingToUs = peersSendingToUs;
    }

    public Integer getPeersGettingFromUs() {
        return peersGettingFromUs;
    }

    public void setPeersGettingFromUs(Integer peersGettingFromUs) {
        this.peersGettingFromUs = peersGettingFromUs;
    }

    public Integer getRateUpload() {
        return rateUpload;
    }

    public void setRateUpload(Integer rateUpload) {
        this.rateUpload = rateUpload;
    }

    public Integer getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Integer addedDate) {
        this.addedDate = addedDate;
    }

    public Integer getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Integer activityDate) {
        this.activityDate = activityDate;
    }

    public Integer getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Integer doneDate) {
        this.doneDate = doneDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", status=").append(status);
        sb.append(", totalSize=").append(totalSize);
        sb.append(", downloadDir=").append(downloadDir);
        sb.append(", error=").append(error);
        sb.append(", errorString=").append(errorString);
        sb.append(", hashString=").append(hashString);
        sb.append(", uploadRatio=").append(uploadRatio);
        sb.append(", percentDone=").append(percentDone);
        sb.append(", peersSendingToUs=").append(peersSendingToUs);
        sb.append(", peersGettingFromUs=").append(peersGettingFromUs);
        sb.append(", rateUpload=").append(rateUpload);
        sb.append(", addedDate=").append(addedDate);
        sb.append(", activityDate=").append(activityDate);
        sb.append(", doneDate=").append(doneDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}