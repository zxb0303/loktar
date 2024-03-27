package com.loktar.domain.newhouse;

import java.io.Serializable;
import java.util.Date;

public class NewHouseHangzhouV2 implements Serializable {
    private String houseId;

    private String name;

    private String nameSpread;

    private Integer price;

    private String type;

    private Double plotRatio;

    private String greenRatio;

    private String coverArea;

    private String bulidArea;

    private String bulidType;

    private Integer totalHouseNum;

    private Integer carParkNum;

    private String area;

    private String areaCode;

    private String plate;

    private String address;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

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

    public String getNameSpread() {
        return nameSpread;
    }

    public void setNameSpread(String nameSpread) {
        this.nameSpread = nameSpread == null ? null : nameSpread.trim();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Double getPlotRatio() {
        return plotRatio;
    }

    public void setPlotRatio(Double plotRatio) {
        this.plotRatio = plotRatio;
    }

    public String getGreenRatio() {
        return greenRatio;
    }

    public void setGreenRatio(String greenRatio) {
        this.greenRatio = greenRatio == null ? null : greenRatio.trim();
    }

    public String getCoverArea() {
        return coverArea;
    }

    public void setCoverArea(String coverArea) {
        this.coverArea = coverArea == null ? null : coverArea.trim();
    }

    public String getBulidArea() {
        return bulidArea;
    }

    public void setBulidArea(String bulidArea) {
        this.bulidArea = bulidArea == null ? null : bulidArea.trim();
    }

    public String getBulidType() {
        return bulidType;
    }

    public void setBulidType(String bulidType) {
        this.bulidType = bulidType == null ? null : bulidType.trim();
    }

    public Integer getTotalHouseNum() {
        return totalHouseNum;
    }

    public void setTotalHouseNum(Integer totalHouseNum) {
        this.totalHouseNum = totalHouseNum;
    }

    public Integer getCarParkNum() {
        return carParkNum;
    }

    public void setCarParkNum(Integer carParkNum) {
        this.carParkNum = carParkNum;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate == null ? null : plate.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", houseId=").append(houseId);
        sb.append(", name=").append(name);
        sb.append(", nameSpread=").append(nameSpread);
        sb.append(", price=").append(price);
        sb.append(", type=").append(type);
        sb.append(", plotRatio=").append(plotRatio);
        sb.append(", greenRatio=").append(greenRatio);
        sb.append(", coverArea=").append(coverArea);
        sb.append(", bulidArea=").append(bulidArea);
        sb.append(", bulidType=").append(bulidType);
        sb.append(", totalHouseNum=").append(totalHouseNum);
        sb.append(", carParkNum=").append(carParkNum);
        sb.append(", area=").append(area);
        sb.append(", areaCode=").append(areaCode);
        sb.append(", plate=").append(plate);
        sb.append(", address=").append(address);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}