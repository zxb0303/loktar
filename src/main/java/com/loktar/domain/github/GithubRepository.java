package com.loktar.domain.github;

import lombok.Data;

import java.io.Serializable;

@Data
public class GithubRepository implements Serializable {
    private Integer repositoryId;

    private String repository;

    private Integer lastTagId;

    private String lastTagName;

    private String publishedAt;

    private Integer status;

    private static final long serialVersionUID = 1L;
}