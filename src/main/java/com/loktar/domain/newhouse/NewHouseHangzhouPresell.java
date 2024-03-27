package com.loktar.domain.newhouse;

import java.io.Serializable;

public class NewHouseHangzhouPresell implements Serializable {
    private String presellId;

    private String houseId;

    private String presellNo;

    private Double price;

    private String date;

    private Integer totalHouseNum;

    private Integer limitHouseNum;

    private Integer soldHouseNum;

    private Integer updateStatus;

    private static final long serialVersionUID = 1L;

    public String getPresellId() {
        return presellId;
    }

    public void setPresellId(String presellId) {
        this.presellId = presellId == null ? null : presellId.trim();
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getPresellNo() {
        return presellNo;
    }

    public void setPresellNo(String presellNo) {
        this.presellNo = presellNo == null ? null : presellNo.trim();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public Integer getTotalHouseNum() {
        return totalHouseNum;
    }

    public void setTotalHouseNum(Integer totalHouseNum) {
        this.totalHouseNum = totalHouseNum;
    }

    public Integer getLimitHouseNum() {
        return limitHouseNum;
    }

    public void setLimitHouseNum(Integer limitHouseNum) {
        this.limitHouseNum = limitHouseNum;
    }

    public Integer getSoldHouseNum() {
        return soldHouseNum;
    }

    public void setSoldHouseNum(Integer soldHouseNum) {
        this.soldHouseNum = soldHouseNum;
    }

    public Integer getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Integer updateStatus) {
        this.updateStatus = updateStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", presellId=").append(presellId);
        sb.append(", houseId=").append(houseId);
        sb.append(", presellNo=").append(presellNo);
        sb.append(", price=").append(price);
        sb.append(", date=").append(date);
        sb.append(", totalHouseNum=").append(totalHouseNum);
        sb.append(", limitHouseNum=").append(limitHouseNum);
        sb.append(", soldHouseNum=").append(soldHouseNum);
        sb.append(", updateStatus=").append(updateStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}