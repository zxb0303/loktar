package com.loktar.domain.cxy;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TiktokCreatorData implements Serializable {
    private Integer userId;

    private Integer accountId;

    private String date;

    private Integer rank;

    private String displayId;

    private Integer incomeCurrent;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId == null ? null : displayId.trim();
    }

    public Integer getIncomeCurrent() {
        return incomeCurrent;
    }

    public void setIncomeCurrent(Integer incomeCurrent) {
        this.incomeCurrent = incomeCurrent;
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
        sb.append(", userId=").append(userId);
        sb.append(", accountId=").append(accountId);
        sb.append(", date=").append(date);
        sb.append(", rank=").append(rank);
        sb.append(", displayId=").append(displayId);
        sb.append(", incomeCurrent=").append(incomeCurrent);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}