package com.loktar.domain.land;

import java.io.Serializable;
import java.time.LocalDate;

public class Land implements Serializable {
    private Integer id;

    private LocalDate date;

    private String city;

    private String area;

    private String landNo;

    private String landName;

    private String status;

    private Float acreage;

    private String landUsage;

    private String volumetricRate;

    private Float dealPrice;

    private Float buildPrice;

    private String premiumRate;

    private String owner;

    private String remark;

    private String detailUrl;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getLandNo() {
        return landNo;
    }

    public void setLandNo(String landNo) {
        this.landNo = landNo == null ? null : landNo.trim();
    }

    public String getLandName() {
        return landName;
    }

    public void setLandName(String landName) {
        this.landName = landName == null ? null : landName.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Float getAcreage() {
        return acreage;
    }

    public void setAcreage(Float acreage) {
        this.acreage = acreage;
    }

    public String getLandUsage() {
        return landUsage;
    }

    public void setLandUsage(String landUsage) {
        this.landUsage = landUsage == null ? null : landUsage.trim();
    }

    public String getVolumetricRate() {
        return volumetricRate;
    }

    public void setVolumetricRate(String volumetricRate) {
        this.volumetricRate = volumetricRate == null ? null : volumetricRate.trim();
    }

    public Float getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(Float dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Float getBuildPrice() {
        return buildPrice;
    }

    public void setBuildPrice(Float buildPrice) {
        this.buildPrice = buildPrice;
    }

    public String getPremiumRate() {
        return premiumRate;
    }

    public void setPremiumRate(String premiumRate) {
        this.premiumRate = premiumRate == null ? null : premiumRate.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl == null ? null : detailUrl.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", date=").append(date);
        sb.append(", city=").append(city);
        sb.append(", area=").append(area);
        sb.append(", landNo=").append(landNo);
        sb.append(", landName=").append(landName);
        sb.append(", status=").append(status);
        sb.append(", acreage=").append(acreage);
        sb.append(", landUsage=").append(landUsage);
        sb.append(", volumetricRate=").append(volumetricRate);
        sb.append(", dealPrice=").append(dealPrice);
        sb.append(", buildPrice=").append(buildPrice);
        sb.append(", premiumRate=").append(premiumRate);
        sb.append(", owner=").append(owner);
        sb.append(", remark=").append(remark);
        sb.append(", detailUrl=").append(detailUrl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}