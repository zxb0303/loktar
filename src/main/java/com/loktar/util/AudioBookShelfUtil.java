package com.loktar.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.audiobookshelf.AbsListeningStats;
import com.loktar.dto.audiobookshelf.AbsUser;
import com.loktar.dto.audiobookshelf.AbsUsersRsp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AudioBookShelfUtil {

    private final LokTarConfig lokTarConfig;

    private final HttpClient httpClient;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public AudioBookShelfUtil(LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
    }

    /**
     * 获取所有用户
     */
    @SneakyThrows
    public List<AbsUser> getUsers() {
        AbsUsersRsp usersRsp = objectMapper.readValue(get("/api/users"), AbsUsersRsp.class);
        return usersRsp.getUsers() == null ? List.of() : usersRsp.getUsers();
    }

    /**
     * 获取所有用户，返回 username -> userId 映射
     */
    public Map<String, String> getUserIdMap() {
        Map<String, String> userIdMap = new HashMap<>();
        for (AbsUser user : getUsers()) {
            userIdMap.put(user.getUsername(), user.getId());
        }
        return userIdMap;
    }

    /**
     * 获取用户当日收听统计数据（today为当日收听秒数）
     */
    @SneakyThrows
    public AbsListeningStats getTodayListeningStats(String userId) {
        return objectMapper.readValue(get("/api/users/" + userId + "/listening-stats"), AbsListeningStats.class);
    }

    /**
     * 更新用户启用状态
     */
    @SneakyThrows
    public void updateUserActive(String userId, boolean isActive) {
        String path = "/api/users/" + userId;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_AUTHORIZATION_NAME, "Bearer " + apiToken())
                .method("PATCH", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(Map.of("isActive", isActive))))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        checkResponse(response, path);
    }

    @SneakyThrows
    private String get(String path) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_AUTHORIZATION_NAME, "Bearer " + apiToken())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        checkResponse(response, path);
        return response.body();
    }

    private void checkResponse(HttpResponse<String> response, String path) {
        if (response.statusCode() == 401) {
            log.error("AudioBookShelf接口调用失败, apiToken无效或所属账号被禁用, path:{}", path);
            throw new IllegalStateException("AudioBookShelf接口调用失败, apiToken无效或所属账号被禁用, path:" + path);
        }
        if (response.statusCode() == 403) {
            log.error("AudioBookShelf接口调用失败, apiToken对应账号无管理员权限, path:{}", path);
            throw new IllegalStateException("AudioBookShelf接口调用失败, apiToken对应账号无管理员权限, path:" + path);
        }
        if (response.statusCode() != 200) {
            log.error("AudioBookShelf接口调用失败, path:{}, statusCode:{}, body:{}", path, response.statusCode(), response.body());
            throw new IllegalStateException("AudioBookShelf接口调用失败, path:" + path + ", statusCode:" + response.statusCode());
        }
    }

    private String apiToken() {
        String apiToken = lokTarConfig.getAudioBookShelf().getApiToken();
        if (StringUtils.isEmpty(apiToken)) {
            throw new IllegalStateException("AudioBookShelf未配置apiToken");
        }
        return apiToken;
    }

    private String baseUrl() {
        return StringUtils.removeEnd(lokTarConfig.getAudioBookShelf().getUrl(), "/");
    }
}
