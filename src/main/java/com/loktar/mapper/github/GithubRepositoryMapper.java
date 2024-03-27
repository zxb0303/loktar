package com.loktar.mapper.github;

import com.loktar.domain.github.GithubRepository;
import java.util.List;

public interface GithubRepositoryMapper {
    int deleteByPrimaryKey(Integer repositoryId);

    int insert(GithubRepository row);

    GithubRepository selectByPrimaryKey(Integer repositoryId);

    List<GithubRepository> selectAll();

    int updateByPrimaryKey(GithubRepository row);

    List<GithubRepository> getNeedCheckGithubRepositorys();
}