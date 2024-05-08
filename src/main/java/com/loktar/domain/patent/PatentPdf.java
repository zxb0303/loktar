package com.loktar.domain.patent;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PatentPdf implements Serializable {
    private String patentId;

    private String applyName;

    private String type;

    private String authNoticeNum;

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

    public String getAuthNoticeNum() {
        return authNoticeNum;
    }

    public void setAuthNoticeNum(String authNoticeNum) {
        this.authNoticeNum = authNoticeNum == null ? null : authNoticeNum.trim();
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
        sb.append(", applyName=").append(applyName);
        sb.append(", type=").append(type);
        sb.append(", authNoticeNum=").append(authNoticeNum);
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