package com.loktar.service.github.impl;


import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.github.GithubRepository;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.github.GithubRepositoryMapper;
import com.loktar.service.github.GithubService;
import com.loktar.util.DateTimeUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GithubServiceImpl implements GithubService {

    private final GithubRepositoryMapper githubRepositoryMapper;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    public GithubServiceImpl(GithubRepositoryMapper githubRepositoryMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.githubRepositoryMapper = githubRepositoryMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void checkRepositoryTag() {
        List<GithubRepository> githubRepositorys = githubRepositoryMapper.getNeedCheckGithubRepositorys();
        for (GithubRepository githubRepository : githubRepositorys) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            GHRelease ghRelease = getGithubRelease(githubRepository.getRepository());
            if (StringUtils.isEmpty(githubRepository.getLastTagName()) || ghRelease.getId() > githubRepository.getLastTagId()) {
                String content = LokTarConstant.NOTICE_TITLE_GITHUB + System.lineSeparator() +
                        System.lineSeparator() +
                        githubRepository.getRepository() + ":" + ghRelease.getTagName() + System.lineSeparator() +
                        System.lineSeparator() +
                        DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
                qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
                githubRepository.setLastTagId((int) ghRelease.getId());
                githubRepository.setLastTagName(ghRelease.getTagName());
                ZonedDateTime localZonedDateTime = ghRelease.getCreatedAt().toInstant().atZone(ZoneId.systemDefault());
                LocalDateTime localDateTime = localZonedDateTime.toLocalDateTime();
                githubRepository.setPublishedAt(DateTimeUtil.getDatetimeStr(localDateTime, DateTimeUtil.FORMATTER_DATESECOND));
                githubRepositoryMapper.updateByPrimaryKey(githubRepository);
            }
        }
    }

    @SneakyThrows
    private GHRelease getGithubRelease(String repository) {
        GitHub github = new GitHubBuilder()
                .withOAuthToken(lokTarConfig.getGithub().getAuthorization())
                .build();
        GHRepository ghRepository = github.getRepository(repository);
        List<GHRelease> releases = ghRepository.listReleases().toList();
        return releases.stream()
                .filter(r -> !r.isPrerelease())
                .findFirst()
                .orElse(releases.getFirst());
    }
}
