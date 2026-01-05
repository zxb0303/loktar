package com.loktar.domain.cxy;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TiktokData implements Serializable {
    private Integer dataId;

    private Integer accountId;

    private String date;

    private Integer todayDiamondCnt;

    private Integer todayLiveCnt;

    private Integer todayNewMemberCnt;

    private Integer monthDiamondCnt;

    private String monthDiamondPct;

    private Integer monthNewCreatorCnt;

    private Integer monthNewCreatorDiamondCnt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
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

    public Integer getTodayDiamondCnt() {
        return todayDiamondCnt;
    }

    public void setTodayDiamondCnt(Integer todayDiamondCnt) {
        this.todayDiamondCnt = todayDiamondCnt;
    }

    public Integer getTodayLiveCnt() {
        return todayLiveCnt;
    }

    public void setTodayLiveCnt(Integer todayLiveCnt) {
        this.todayLiveCnt = todayLiveCnt;
    }

    public Integer getTodayNewMemberCnt() {
        return todayNewMemberCnt;
    }

    public void setTodayNewMemberCnt(Integer todayNewMemberCnt) {
        this.todayNewMemberCnt = todayNewMemberCnt;
    }

    public Integer getMonthDiamondCnt() {
        return monthDiamondCnt;
    }

    public void setMonthDiamondCnt(Integer monthDiamondCnt) {
        this.monthDiamondCnt = monthDiamondCnt;
    }

    public String getMonthDiamondPct() {
        return monthDiamondPct;
    }

    public void setMonthDiamondPct(String monthDiamondPct) {
        this.monthDiamondPct = monthDiamondPct == null ? null : monthDiamondPct.trim();
    }

    public Integer getMonthNewCreatorCnt() {
        return monthNewCreatorCnt;
    }

    public void setMonthNewCreatorCnt(Integer monthNewCreatorCnt) {
        this.monthNewCreatorCnt = monthNewCreatorCnt;
    }

    public Integer getMonthNewCreatorDiamondCnt() {
        return monthNewCreatorDiamondCnt;
    }

    public void setMonthNewCreatorDiamondCnt(Integer monthNewCreatorDiamondCnt) {
        this.monthNewCreatorDiamondCnt = monthNewCreatorDiamondCnt;
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
        sb.append(", dataId=").append(dataId);
        sb.append(", accountId=").append(accountId);
        sb.append(", date=").append(date);
        sb.append(", todayDiamondCnt=").append(todayDiamondCnt);
        sb.append(", todayLiveCnt=").append(todayLiveCnt);
        sb.append(", todayNewMemberCnt=").append(todayNewMemberCnt);
        sb.append(", monthDiamondCnt=").append(monthDiamondCnt);
        sb.append(", monthDiamondPct=").append(monthDiamondPct);
        sb.append(", monthNewCreatorCnt=").append(monthNewCreatorCnt);
        sb.append(", monthNewCreatorDiamondCnt=").append(monthNewCreatorDiamondCnt);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}