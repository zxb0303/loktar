package com.loktar.domain.patent;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PatentPdfApply implements Serializable {
    private String applyId;

    private String applyName;

    private Integer patentAuthCount2020;

    private Integer patentAuthCount2021;

    private Integer patentAuthCount2022;

    private Integer patentAuthCount2023;

    private Integer patentAuthCount2024;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId == null ? null : applyId.trim();
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName == null ? null : applyName.trim();
    }

    public Integer getPatentAuthCount2020() {
        return patentAuthCount2020;
    }

    public void setPatentAuthCount2020(Integer patentAuthCount2020) {
        this.patentAuthCount2020 = patentAuthCount2020;
    }

    public Integer getPatentAuthCount2021() {
        return patentAuthCount2021;
    }

    public void setPatentAuthCount2021(Integer patentAuthCount2021) {
        this.patentAuthCount2021 = patentAuthCount2021;
    }

    public Integer getPatentAuthCount2022() {
        return patentAuthCount2022;
    }

    public void setPatentAuthCount2022(Integer patentAuthCount2022) {
        this.patentAuthCount2022 = patentAuthCount2022;
    }

    public Integer getPatentAuthCount2023() {
        return patentAuthCount2023;
    }

    public void setPatentAuthCount2023(Integer patentAuthCount2023) {
        this.patentAuthCount2023 = patentAuthCount2023;
    }

    public Integer getPatentAuthCount2024() {
        return patentAuthCount2024;
    }

    public void setPatentAuthCount2024(Integer patentAuthCount2024) {
        this.patentAuthCount2024 = patentAuthCount2024;
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
        sb.append(", applyId=").append(applyId);
        sb.append(", applyName=").append(applyName);
        sb.append(", patentAuthCount2020=").append(patentAuthCount2020);
        sb.append(", patentAuthCount2021=").append(patentAuthCount2021);
        sb.append(", patentAuthCount2022=").append(patentAuthCount2022);
        sb.append(", patentAuthCount2023=").append(patentAuthCount2023);
        sb.append(", patentAuthCount2024=").append(patentAuthCount2024);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}