package com.loktar.domain.qywx;

import java.io.Serializable;
import java.time.LocalDateTime;

public class QywxChatgptMsg implements Serializable {
    private Integer id;

    private String fromUserName;

    private String role;

    private String filename;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totaltokens;

    private LocalDateTime createTime;

    private String text;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName == null ? null : fromUserName.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role == null ? null : role.trim();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotaltokens() {
        return totaltokens;
    }

    public void setTotaltokens(Integer totaltokens) {
        this.totaltokens = totaltokens;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", fromUserName=").append(fromUserName);
        sb.append(", role=").append(role);
        sb.append(", filename=").append(filename);
        sb.append(", promptTokens=").append(promptTokens);
        sb.append(", completionTokens=").append(completionTokens);
        sb.append(", totaltokens=").append(totaltokens);
        sb.append(", createTime=").append(createTime);
        sb.append(", text=").append(text);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}