package com.loktar.dto.github;

import lombok.Data;

import java.util.Date;

@Data
public class GithubRelease {
    private int id;
    private String tagName;
    private Date createdAt;
    private boolean prerelease;
}
