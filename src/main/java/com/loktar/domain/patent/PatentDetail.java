package com.loktar.domain.patent;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PatentDetail implements Serializable {
    private String patentId;

    private String name;

    private String applyName;

    private String type;

    private String applyDate;

    private String pubNoticeNum;

    private String authNoticeNum;

    private String legalStatus;

    private String caseStatus;

    private String authNoticeDate;

    private String mainCategoryNum;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public String getPatentId() {
        return patentId;
    }

    public void setPatentId(String patentId) {
        this.patentId = patentId == null ? null : patentId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName == null ? null : applyName.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate == null ? null : applyDate.trim();
    }

    public String getPubNoticeNum() {
        return pubNoticeNum;
    }

    public void setPubNoticeNum(String pubNoticeNum) {
        this.pubNoticeNum = pubNoticeNum == null ? null : pubNoticeNum.trim();
    }

    public String getAuthNoticeNum() {
        return authNoticeNum;
    }

    public void setAuthNoticeNum(String authNoticeNum) {
        this.authNoticeNum = authNoticeNum == null ? null : authNoticeNum.trim();
    }

    public String getLegalStatus() {
        return legalStatus;
    }

    public void setLegalStatus(String legalStatus) {
        this.legalStatus = legalStatus == null ? null : legalStatus.trim();
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus == null ? null : caseStatus.trim();
    }

    public String getAuthNoticeDate() {
        return authNoticeDate;
    }

    public void setAuthNoticeDate(String authNoticeDate) {
        this.authNoticeDate = authNoticeDate == null ? null : authNoticeDate.trim();
    }

    public String getMainCategoryNum() {
        return mainCategoryNum;
    }

    public void setMainCategoryNum(String mainCategoryNum) {
        this.mainCategoryNum = mainCategoryNum == null ? null : mainCategoryNum.trim();
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
        sb.append(", patentId=").append(patentId);
        sb.append(", name=").append(name);
        sb.append(", applyName=").append(applyName);
        sb.append(", type=").append(type);
        sb.append(", applyDate=").append(applyDate);
        sb.append(", pubNoticeNum=").append(pubNoticeNum);
        sb.append(", authNoticeNum=").append(authNoticeNum);
        sb.append(", legalStatus=").append(legalStatus);
        sb.append(", caseStatus=").append(caseStatus);
        sb.append(", authNoticeDate=").append(authNoticeDate);
        sb.append(", mainCategoryNum=").append(mainCategoryNum);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}