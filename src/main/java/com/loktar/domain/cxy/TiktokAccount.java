package com.loktar.domain.cxy;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TiktokAccount implements Serializable {
    private Integer accountId;

    private String country;

    private String email;

    private String password;

    private String factionId;

    private Integer status;

    private String excelStart;

    private String excelEnd;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getFactionId() {
        return factionId;
    }

    public void setFactionId(String factionId) {
        this.factionId = factionId == null ? null : factionId.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExcelStart() {
        return excelStart;
    }

    public void setExcelStart(String excelStart) {
        this.excelStart = excelStart == null ? null : excelStart.trim();
    }

    public String getExcelEnd() {
        return excelEnd;
    }

    public void setExcelEnd(String excelEnd) {
        this.excelEnd = excelEnd == null ? null : excelEnd.trim();
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
        sb.append(", accountId=").append(accountId);
        sb.append(", country=").append(country);
        sb.append(", email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", factionId=").append(factionId);
        sb.append(", status=").append(status);
        sb.append(", excelStart=").append(excelStart);
        sb.append(", excelEnd=").append(excelEnd);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}