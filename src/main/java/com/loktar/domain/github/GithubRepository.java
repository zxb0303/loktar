package com.loktar.domain.github;

import java.io.Serializable;

public class GithubRepository implements Serializable {
    private Integer repositoryId;

    private String repository;

    private Integer lastTagId;

    private String lastTagName;

    private String publishedAt;

    private Integer status;

    private static final long serialVersionUID = 1L;

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository == null ? null : repository.trim();
    }

    public Integer getLastTagId() {
        return lastTagId;
    }

    public void setLastTagId(Integer lastTagId) {
        this.lastTagId = lastTagId;
    }

    public String getLastTagName() {
        return lastTagName;
    }

    public void setLastTagName(String lastTagName) {
        this.lastTagName = lastTagName == null ? null : lastTagName.trim();
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt == null ? null : publishedAt.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", repositoryId=").append(repositoryId);
        sb.append(", repository=").append(repository);
        sb.append(", lastTagId=").append(lastTagId);
        sb.append(", lastTagName=").append(lastTagName);
        sb.append(", publishedAt=").append(publishedAt);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}