package com.loktar.dto.github;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class GithubRelease {
    private int id;
    private String tagName;
    private ZonedDateTime createdAt;
    private boolean prerelease;
}
