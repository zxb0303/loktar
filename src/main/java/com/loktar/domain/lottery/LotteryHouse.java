package com.loktar.domain.lottery;

import java.io.Serializable;

public class LotteryHouse implements Serializable {
    private String houseId;

    private String houseName;

    private Integer totalPeopleNum;

    private Integer totalHouseNum;

    private Integer elitePeopleNum;

    private Integer eliteHouseNum;

    private String eliteChance;

    private Integer homelessPeopleNum;

    private Integer homelessHouseNum;

    private String homelessChance;

    private Integer unhomelessPeopleNum;

    private Integer unhomelessHouseNum;

    private String unhomelessChance;

    private Integer status;

    private String lotteryTime;

    private static final long serialVersionUID = 1L;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName == null ? null : houseName.trim();
    }

    public Integer getTotalPeopleNum() {
        return totalPeopleNum;
    }

    public void setTotalPeopleNum(Integer totalPeopleNum) {
        this.totalPeopleNum = totalPeopleNum;
    }

    public Integer getTotalHouseNum() {
        return totalHouseNum;
    }

    public void setTotalHouseNum(Integer totalHouseNum) {
        this.totalHouseNum = totalHouseNum;
    }

    public Integer getElitePeopleNum() {
        return elitePeopleNum;
    }

    public void setElitePeopleNum(Integer elitePeopleNum) {
        this.elitePeopleNum = elitePeopleNum;
    }

    public Integer getEliteHouseNum() {
        return eliteHouseNum;
    }

    public void setEliteHouseNum(Integer eliteHouseNum) {
        this.eliteHouseNum = eliteHouseNum;
    }

    public String getEliteChance() {
        return eliteChance;
    }

    public void setEliteChance(String eliteChance) {
        this.eliteChance = eliteChance == null ? null : eliteChance.trim();
    }

    public Integer getHomelessPeopleNum() {
        return homelessPeopleNum;
    }

    public void setHomelessPeopleNum(Integer homelessPeopleNum) {
        this.homelessPeopleNum = homelessPeopleNum;
    }

    public Integer getHomelessHouseNum() {
        return homelessHouseNum;
    }

    public void setHomelessHouseNum(Integer homelessHouseNum) {
        this.homelessHouseNum = homelessHouseNum;
    }

    public String getHomelessChance() {
        return homelessChance;
    }

    public void setHomelessChance(String homelessChance) {
        this.homelessChance = homelessChance == null ? null : homelessChance.trim();
    }

    public Integer getUnhomelessPeopleNum() {
        return unhomelessPeopleNum;
    }

    public void setUnhomelessPeopleNum(Integer unhomelessPeopleNum) {
        this.unhomelessPeopleNum = unhomelessPeopleNum;
    }

    public Integer getUnhomelessHouseNum() {
        return unhomelessHouseNum;
    }

    public void setUnhomelessHouseNum(Integer unhomelessHouseNum) {
        this.unhomelessHouseNum = unhomelessHouseNum;
    }

    public String getUnhomelessChance() {
        return unhomelessChance;
    }

    public void setUnhomelessChance(String unhomelessChance) {
        this.unhomelessChance = unhomelessChance == null ? null : unhomelessChance.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLotteryTime() {
        return lotteryTime;
    }

    public void setLotteryTime(String lotteryTime) {
        this.lotteryTime = lotteryTime == null ? null : lotteryTime.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", houseId=").append(houseId);
        sb.append(", houseName=").append(houseName);
        sb.append(", totalPeopleNum=").append(totalPeopleNum);
        sb.append(", totalHouseNum=").append(totalHouseNum);
        sb.append(", elitePeopleNum=").append(elitePeopleNum);
        sb.append(", eliteHouseNum=").append(eliteHouseNum);
        sb.append(", eliteChance=").append(eliteChance);
        sb.append(", homelessPeopleNum=").append(homelessPeopleNum);
        sb.append(", homelessHouseNum=").append(homelessHouseNum);
        sb.append(", homelessChance=").append(homelessChance);
        sb.append(", unhomelessPeopleNum=").append(unhomelessPeopleNum);
        sb.append(", unhomelessHouseNum=").append(unhomelessHouseNum);
        sb.append(", unhomelessChance=").append(unhomelessChance);
        sb.append(", status=").append(status);
        sb.append(", lotteryTime=").append(lotteryTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}