package com.loktar.domain.newhouse;

import java.io.Serializable;
import java.util.Date;

public class NewHouseHangzhouV3 implements Serializable {
    private String houseId;

    private String tempHouseId;

    private String name;

    private String useful;

    private String address;

    private String company;

    private String phone;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getTempHouseId() {
        return tempHouseId;
    }

    public void setTempHouseId(String tempHouseId) {
        this.tempHouseId = tempHouseId == null ? null : tempHouseId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUseful() {
        return useful;
    }

    public void setUseful(String useful) {
        this.useful = useful == null ? null : useful.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
        sb.append(", tempHouseId=").append(tempHouseId);
        sb.append(", name=").append(name);
        sb.append(", useful=").append(useful);
        sb.append(", address=").append(address);
        sb.append(", company=").append(company);
        sb.append(", phone=").append(phone);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}