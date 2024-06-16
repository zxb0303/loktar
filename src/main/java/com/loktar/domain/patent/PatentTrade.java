package com.loktar.domain.patent;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PatentTrade implements Serializable {
    private String patentId;

    private String fromApplyName;

    private String toApplyName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public String getPatentId() {
        return patentId;
    }

    public void setPatentId(String patentId) {
        this.patentId = patentId == null ? null : patentId.trim();
    }

    public String getFromApplyName() {
        return fromApplyName;
    }

    public void setFromApplyName(String fromApplyName) {
        this.fromApplyName = fromApplyName == null ? null : fromApplyName.trim();
    }

    public String getToApplyName() {
        return toApplyName;
    }

    public void setToApplyName(String toApplyName) {
        this.toApplyName = toApplyName == null ? null : toApplyName.trim();
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", patentId=").append(patentId);
        sb.append(", fromApplyName=").append(fromApplyName);
        sb.append(", toApplyName=").append(toApplyName);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}