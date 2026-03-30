package com.loktar.domain.investment;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EquityIndexDividendYieldDaily implements Serializable {
    private Integer id;

    private String equityIndex;

    private String equityIndexName;

    private String date;

    private Float dividendYield;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEquityIndex() {
        return equityIndex;
    }

    public void setEquityIndex(String equityIndex) {
        this.equityIndex = equityIndex == null ? null : equityIndex.trim();
    }

    public String getEquityIndexName() {
        return equityIndexName;
    }

    public void setEquityIndexName(String equityIndexName) {
        this.equityIndexName = equityIndexName == null ? null : equityIndexName.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public Float getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(Float dividendYield) {
        this.dividendYield = dividendYield;
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
        sb.append(", id=").append(id);
        sb.append(", equityIndex=").append(equityIndex);
        sb.append(", equityIndexName=").append(equityIndexName);
        sb.append(", date=").append(date);
        sb.append(", dividendYield=").append(dividendYield);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}