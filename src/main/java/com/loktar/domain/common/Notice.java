package com.loktar.domain.common;

import java.io.Serializable;

public class Notice implements Serializable {
    private Integer noticeId;

    private String noticeTitle;

    private String noticeContent;

    private String noticeTime;

    private String noticeUser;

    private static final long serialVersionUID = 1L;

    public Integer getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle == null ? null : noticeTitle.trim();
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent == null ? null : noticeContent.trim();
    }

    public String getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(String noticeTime) {
        this.noticeTime = noticeTime == null ? null : noticeTime.trim();
    }

    public String getNoticeUser() {
        return noticeUser;
    }

    public void setNoticeUser(String noticeUser) {
        this.noticeUser = noticeUser == null ? null : noticeUser.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", noticeId=").append(noticeId);
        sb.append(", noticeTitle=").append(noticeTitle);
        sb.append(", noticeContent=").append(noticeContent);
        sb.append(", noticeTime=").append(noticeTime);
        sb.append(", noticeUser=").append(noticeUser);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}