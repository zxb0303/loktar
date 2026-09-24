package com.loktar.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.audiobookshelf.AbsListeningStats;
import com.loktar.dto.audiobookshelf.AbsOpenSessionsRsp;
import com.loktar.dto.audiobookshelf.AbsPlaybackSession;
import com.loktar.dto.audiobookshelf.AbsUser;
import com.loktar.dto.audiobookshelf.AbsUsersRsp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AudioBookShelfUtil {

    private final LokTarConfig lokTarConfig;

    private final HttpClient httpClient;

    private static final JsonMapper objectMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .changeDefaultPropertyInclusion(inclusion -> inclusion
                    .withValueInclusion(JsonInclude.Include.NON_NULL)
                    .withContentInclusion(JsonInclude.Include.NON_NULL))
            .build();

    public AudioBookShelfUtil(LokTarConfig lokTarConfig, HttpClient httpClient) {
        this.lokTarConfig = lokTarConfig;
        this.httpClient = httpClient;
    }

    /**
     * 获取所有用户
     */
    public List<AbsUser> getUsers() {
        AbsUsersRsp usersRsp = objectMapper.readValue(get("/api/users"), AbsUsersRsp.class);
        return usersRsp.getUsers() == null ? List.of() : usersRsp.getUsers();
    }

    /**
     * 根据用户名获取用户，未找到时返回null
     */
    public AbsUser getUser(String username) {
        for (AbsUser user : getUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 切换指定用户的启用状态（启用中则禁用，否则启用），返回切换后的状态
     */
    public boolean switchUserActive(String username) {
        AbsUser targetUser = getUser(username);
        boolean toActive = !Boolean.TRUE.equals(targetUser.getIsActive());
        updateUserActive(targetUser.getId(), toActive);
        return toActive;
    }

    /**
     * 获取用户当日收听统计数据（today为当日收听秒数）
     */
    public AbsListeningStats getTodayListeningStats(String userId) {
        return objectMapper.readValue(get("/api/users/" + userId + "/listening-stats"), AbsListeningStats.class);
    }

    /**
     * 获取当前打开的播放会话（暂停中的会话仍保持打开，需结合播放进度是否变化判断是否正在播放）
     */
    public List<AbsPlaybackSession> getOpenSessions() {
        AbsOpenSessionsRsp openSessionsRsp = objectMapper.readValue(get("/api/sessions/open"), AbsOpenSessionsRsp.class);
        return openSessionsRsp.getSessions() == null ? List.of() : openSessionsRsp.getSessions();
    }

    /**
     * 更新用户启用状态
     */
    @SneakyThrows
    public void updateUserActive(String userId, boolean isActive) {
        String path = "/api/users/" + userId;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                // ABS 内网入口无法兼容 JDK 默认的 h2c 升级，会在返回响应头前断连（EOF）。
                // 在请求级指定 HTTP/1.1，避免影响共享 HttpClient 的其他调用方。
                .version(HttpClient.Version.HTTP_1_1)
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
                // ABS 内网入口无法兼容 JDK 默认的 h2c 升级，会在返回响应头前断连（EOF）。
                // 在请求级指定 HTTP/1.1，避免影响共享 HttpClient 的其他调用方。
                .version(HttpClient.Version.HTTP_1_1)
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
