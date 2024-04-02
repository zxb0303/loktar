package com.loktar.service.github.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.github.GithubRepository;
import com.loktar.dto.github.GithubRelease;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.github.GithubRepositoryMapper;
import com.loktar.service.github.GithubService;
import com.loktar.util.DateUtil;
import com.loktar.util.wx.qywx.QywxApi;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;

@Service
public class GithubServiceImpl implements GithubService {

    private final GithubRepositoryMapper githubRepositoryMapper;
    private final QywxApi qywxApi;
    private final String URL = "https://api.github.com/repos/{0}/releases";


    public GithubServiceImpl(GithubRepositoryMapper githubRepositoryMapper, QywxApi qywxApi) {
        this.githubRepositoryMapper = githubRepositoryMapper;
        this.qywxApi = qywxApi;
    }

    @Override
    @Transactional
    public void checkRepositoryTag() {
        List<GithubRepository> githubRepositorys = githubRepositoryMapper.getNeedCheckGithubRepositorys();
        for (GithubRepository githubRepository : githubRepositorys) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            GithubRelease githubRelease = getGithubReleaseDTO(githubRepository.getRepository());
            if (StringUtils.isEmpty(githubRepository.getLastTagName()) || githubRelease.getId() > githubRepository.getLastTagId()) {
                String content = new StringBuilder().append(LokTarConstant.NOTICE_TITLE_GITHUB).append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(githubRepository.getRepository()).append(":").append(githubRepository.getLastTagName())
                        .append(System.lineSeparator())
                        .append(DateUtil.getMinuteSysDate()).toString();
                qywxApi.sendTextMsg(new AgentMsgText(LokTarPrivateConstant.NOTICE_ZXB, LokTarPrivateConstant.AGENT002ID, content));
                githubRepository.setLastTagId(githubRelease.getId());
                githubRepository.setLastTagName(githubRelease.getTagName());
                githubRepository.setPublishedAt(DateUtil.format(githubRelease.getCreatedAt(), DateUtil.DATEFORMATSECOND));
                githubRepositoryMapper.updateByPrimaryKey(githubRepository);
            }
        }
    }

    @SneakyThrows
    private GithubRelease getGithubReleaseDTO(String registory) {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(MessageFormat.format(URL, registory));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("authorization", LokTarPrivateConstant.GITHUB_AUTHORIZATION)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        List<GithubRelease> githubReleases = objectMapper.readValue(responseBody, new TypeReference<List<GithubRelease>>() {
        });
        for (GithubRelease githubRelease : githubReleases) {
            if (!githubRelease.isPrerelease()) {
                return githubRelease;
            }
        }
        return githubReleases.get(0);
    }
}
