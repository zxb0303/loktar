package com.loktar.domain.newhouse;

import java.io.Serializable;

public class NewHouseHangzhouDetail implements Serializable {
    private String detailId;

    private String houseId;

    private String presellId;

    private String buildNo;

    private String unitNo;

    private String roomNo;

    private String buildArea;

    private String innerArea;

    private String areaRate;

    private String recordPrice;

    private String fixPrice;

    private String price;

    private String status;

    private static final long serialVersionUID = 1L;

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId == null ? null : detailId.trim();
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getPresellId() {
        return presellId;
    }

    public void setPresellId(String presellId) {
        this.presellId = presellId == null ? null : presellId.trim();
    }

    public String getBuildNo() {
        return buildNo;
    }

    public void setBuildNo(String buildNo) {
        this.buildNo = buildNo == null ? null : buildNo.trim();
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo == null ? null : unitNo.trim();
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo == null ? null : roomNo.trim();
    }

    public String getBuildArea() {
        return buildArea;
    }

    public void setBuildArea(String buildArea) {
        this.buildArea = buildArea == null ? null : buildArea.trim();
    }

    public String getInnerArea() {
        return innerArea;
    }

    public void setInnerArea(String innerArea) {
        this.innerArea = innerArea == null ? null : innerArea.trim();
    }

    public String getAreaRate() {
        return areaRate;
    }

    public void setAreaRate(String areaRate) {
        this.areaRate = areaRate == null ? null : areaRate.trim();
    }

    public String getRecordPrice() {
        return recordPrice;
    }

    public void setRecordPrice(String recordPrice) {
        this.recordPrice = recordPrice == null ? null : recordPrice.trim();
    }

    public String getFixPrice() {
        return fixPrice;
    }

    public void setFixPrice(String fixPrice) {
        this.fixPrice = fixPrice == null ? null : fixPrice.trim();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price == null ? null : price.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", detailId=").append(detailId);
        sb.append(", houseId=").append(houseId);
        sb.append(", presellId=").append(presellId);
        sb.append(", buildNo=").append(buildNo);
        sb.append(", unitNo=").append(unitNo);
        sb.append(", roomNo=").append(roomNo);
        sb.append(", buildArea=").append(buildArea);
        sb.append(", innerArea=").append(innerArea);
        sb.append(", areaRate=").append(areaRate);
        sb.append(", recordPrice=").append(recordPrice);
        sb.append(", fixPrice=").append(fixPrice);
        sb.append(", price=").append(price);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}