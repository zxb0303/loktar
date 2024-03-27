package com.loktar.domain.lottery;

import java.io.Serializable;

public class LotteryPeople implements Serializable {
    private String peopleId;

    private String serialNum;

    private String houseId;

    private String name;

    private String identityNum;

    private Integer familyType;

    private Integer hasOtherPeople;

    private Integer lotteryRank;

    private String recordNum;

    private static final long serialVersionUID = 1L;

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId == null ? null : peopleId.trim();
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum == null ? null : serialNum.trim();
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(String identityNum) {
        this.identityNum = identityNum == null ? null : identityNum.trim();
    }

    public Integer getFamilyType() {
        return familyType;
    }

    public void setFamilyType(Integer familyType) {
        this.familyType = familyType;
    }

    public Integer getHasOtherPeople() {
        return hasOtherPeople;
    }

    public void setHasOtherPeople(Integer hasOtherPeople) {
        this.hasOtherPeople = hasOtherPeople;
    }

    public Integer getLotteryRank() {
        return lotteryRank;
    }

    public void setLotteryRank(Integer lotteryRank) {
        this.lotteryRank = lotteryRank;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum == null ? null : recordNum.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", peopleId=").append(peopleId);
        sb.append(", serialNum=").append(serialNum);
        sb.append(", houseId=").append(houseId);
        sb.append(", name=").append(name);
        sb.append(", identityNum=").append(identityNum);
        sb.append(", familyType=").append(familyType);
        sb.append(", hasOtherPeople=").append(hasOtherPeople);
        sb.append(", lotteryRank=").append(lotteryRank);
        sb.append(", recordNum=").append(recordNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}