package com.loktar.domain.newhouse;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NewHouseHangzhouV3Detail implements Serializable {
    private String detailId;

    private String buildId;

    private String presellId;

    private String houseId;

    private String buildNo;

    private String unitNo;

    private String roomNo;

    private String buildArea;

    private String innerArea;

    private String areaRate;

    private String unitPrice;

    private String totalPrice;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId == null ? null : detailId.trim();
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId == null ? null : buildId.trim();
    }

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

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice == null ? null : unitPrice.trim();
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice == null ? null : totalPrice.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        sb.append(", detailId=").append(detailId);
        sb.append(", buildId=").append(buildId);
        sb.append(", presellId=").append(presellId);
        sb.append(", houseId=").append(houseId);
        sb.append(", buildNo=").append(buildNo);
        sb.append(", unitNo=").append(unitNo);
        sb.append(", roomNo=").append(roomNo);
        sb.append(", buildArea=").append(buildArea);
        sb.append(", innerArea=").append(innerArea);
        sb.append(", areaRate=").append(areaRate);
        sb.append(", unitPrice=").append(unitPrice);
        sb.append(", totalPrice=").append(totalPrice);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}