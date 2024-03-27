package com.loktar.domain.lottery;

import java.io.Serializable;

public class LotteryOtherPeople implements Serializable {
    private String otherPeopleId;

    private String houseId;

    private String peopleId;

    private String name;

    private String identityNum;

    private String recordNum;

    private static final long serialVersionUID = 1L;

    public String getOtherPeopleId() {
        return otherPeopleId;
    }

    public void setOtherPeopleId(String otherPeopleId) {
        this.otherPeopleId = otherPeopleId == null ? null : otherPeopleId.trim();
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId == null ? null : peopleId.trim();
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
        sb.append(", otherPeopleId=").append(otherPeopleId);
        sb.append(", houseId=").append(houseId);
        sb.append(", peopleId=").append(peopleId);
        sb.append(", name=").append(name);
        sb.append(", identityNum=").append(identityNum);
        sb.append(", recordNum=").append(recordNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}