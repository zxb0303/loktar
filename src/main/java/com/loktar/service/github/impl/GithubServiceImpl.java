package com.loktar.service.github.impl;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.github.GithubRepository;
import com.loktar.dto.github.GithubRelease;
import com.loktar.dto.wx.agentmsg.AgentMsgText;
import com.loktar.mapper.github.GithubRepositoryMapper;
import com.loktar.service.github.GithubService;
import com.loktar.util.DateTimeUtil;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GithubServiceImpl implements GithubService {

    private final GithubRepositoryMapper githubRepositoryMapper;
    private final QywxApi qywxApi;
    private final String URL = "https://api.github.com/repos/{0}/releases";
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final LokTarConfig lokTarConfig;

    public GithubServiceImpl(GithubRepositoryMapper githubRepositoryMapper, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.githubRepositoryMapper = githubRepositoryMapper;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new JavaTimeModule());
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
            GithubRelease githubRelease = getGithubRelease(githubRepository.getRepository());
            if (StringUtils.isEmpty(githubRepository.getLastTagName()) || githubRelease.getId() > githubRepository.getLastTagId()) {
                String content = LokTarConstant.NOTICE_TITLE_GITHUB + System.lineSeparator() +
                        System.lineSeparator() +
                        githubRepository.getRepository() + ":" + githubRelease.getTagName() + System.lineSeparator() +
                        System.lineSeparator() +
                        DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATEMINUTE);
                qywxApi.sendTextMsg(new AgentMsgText(lokTarConfig.getQywx().getNoticeZxb(), lokTarConfig.getQywx().getAgent002Id(), content));
                githubRepository.setLastTagId(githubRelease.getId());
                githubRepository.setLastTagName(githubRelease.getTagName());
                ZonedDateTime localZonedDateTime = githubRelease.getCreatedAt().withZoneSameInstant(ZoneId.systemDefault());
                LocalDateTime localDateTime = localZonedDateTime.toLocalDateTime();
                githubRepository.setPublishedAt(DateTimeUtil.getDatetimeStr(localDateTime, DateTimeUtil.FORMATTER_DATESECOND));
                githubRepositoryMapper.updateByPrimaryKey(githubRepository);
            }
        }
    }

    @SneakyThrows
    private GithubRelease getGithubRelease(String registory) {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(MessageFormat.format(URL, registory));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("authorization", lokTarConfig.getGithub().getAuthorization())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        List<GithubRelease> githubReleases = objectMapper.readValue(responseBody, new TypeReference<>() {
        });
        for (GithubRelease githubRelease : githubReleases) {
            if (!githubRelease.isPrerelease()) {
                return githubRelease;
            }
        }
        return githubReleases.getFirst();
    }
}
