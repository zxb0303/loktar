package com.loktar.util.wx.qywx;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.domain.qywx.QywxMenu;
import com.loktar.dto.wx.*;
import com.loktar.dto.wx.agentmsg.*;
import com.loktar.mapper.qywx.QywxMenuMapper;
import com.loktar.util.RedisUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;

@Component
public class QywxApi {
    private final static String KEY_ACCESSTOKEN = "qywx_accessToken_";

    private final static String ACCESSTOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={0}&corpsecret={1}";

    private final static String SEND_AGENTMSG_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={0}";

    private final static String MENU_CREATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token={0}&agentid={1}";

    private final static String UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token={0}&type={1}";

    private final static String GET_MEDIA_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token={0}&media_id={1}";

    private final static String FORM_NAME = "media";

    private final RedisUtil redisUtil;

    private final QywxMenuMapper qywxMenuMapper;

    public QywxApi(RedisUtil redisUtil, QywxMenuMapper qywxMenuMapper) {
        this.redisUtil = redisUtil;
        this.qywxMenuMapper = qywxMenuMapper;
    }

    /**
     * 获取accessToken
     */
    @SneakyThrows
    public AccessToken accessToken(String agentId) {
        AccessToken accessToken = (AccessToken) redisUtil.get(KEY_ACCESSTOKEN + agentId);
        if (Objects.nonNull(accessToken)) {
            return accessToken;
        }
        return refreshAccessToken(agentId);
    }

    /**
     * 刷新accessToken
     */
    @SneakyThrows
    private AccessToken refreshAccessToken(String agentId) {
        String secret = LokTarPrivateConstant.AGENTMAP.get(agentId);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(ACCESSTOKEN_URL, LokTarPrivateConstant.CORPID, secret)))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        AccessToken accessToken = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).readValue(response.body(), AccessToken.class);
        if (!StringUtils.isEmpty(accessToken.getAccessToken())) {
            redisUtil.set(KEY_ACCESSTOKEN + agentId, accessToken, accessToken.getExpiresIn());
        }
        return accessToken;
    }

    public AgentMsgRsp sendTextMsg(AgentMsgText agentMsgText) {
        return sendMessage(agentMsgText, agentMsgText.getAgentid());
    }

    public AgentMsgRsp sendTextCardMsg(AgentMsgTextCard agentMsgTextCard) {
        return sendMessage(agentMsgTextCard, agentMsgTextCard.getAgentid());
    }

    public AgentMsgRsp sendVoiceMsg(AgentMsgVoice agentMsgVoice) {
        return sendMessage(agentMsgVoice, agentMsgVoice.getAgentid());
    }

    public AgentMsgRsp sendFileMsg(AgentMsgFile agentMsgFile) {
        return sendMessage(agentMsgFile, agentMsgFile.getAgentid());
    }

    public AgentMsgRsp sendMarkdownMsg(AgentMsgMarkdown agentMsgMarkdown) {
        return sendMessage(agentMsgMarkdown, agentMsgMarkdown.getAgentid());
    }

    @SneakyThrows
    private <T> AgentMsgRsp sendMessage(T message, String agentId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String requestbody = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).writeValueAsString(message);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(SEND_AGENTMSG_URL, accessToken(agentId).getAccessToken())))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(requestbody))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), AgentMsgRsp.class);
    }

    //TODO need test
    @SneakyThrows
    public BaseResult createAgentMenu(String agentId) {
        List<QywxMenu> qywxMenus = qywxMenuMapper.selectAllByAgentId(agentId);
        Map<String, Menu.Button> buttonsMap = new HashMap<>();
        for (QywxMenu qywxMenu : qywxMenus) {
            // 一级菜单且有子菜单
            if (qywxMenu.getButtonLevel() == 1 && qywxMenu.getHasSubButton() == 1) {
                buttonsMap.put(qywxMenu.getButton(), new Menu.ParentButton(qywxMenu.getName(), qywxMenu.getKey(), qywxMenu.getOrder()));
                // 一级菜单没有子菜单
            } else if (qywxMenu.getButtonLevel() == 1) {
                buttonsMap.put(qywxMenu.getButton(), getButton(qywxMenu));
                // 二级菜单
            } else {
                ((Menu.ParentButton) buttonsMap.get(qywxMenu.getButton())).getSubButton().add(getButton(qywxMenu));
            }
        }
        List<Menu.Button> buttons = new ArrayList<>(buttonsMap.values());
        buttons.sort(Comparator.comparingInt(Menu.Button::getOrder));
        Menu wxMenu = new Menu(buttons);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(MENU_CREATE_URL, accessToken(agentId).getAccessToken())))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(wxMenu)))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        BaseResult baseResult = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE).readValue(response.body(), AgentMsgRsp.class);
        return baseResult;
    }

    private Menu.Button getButton(QywxMenu config) {
        KeyButtonType type = KeyButtonType.getByName((config.getType()));
        switch (type) {
            case KeyButtonType.VIEW:
                return new Menu.ViewButton(config.getName(),config.getUrl(), config.getOrder());
            default:
                return new Menu.KeyButton(type,config.getName(), config.getKey(),
                        config.getOrder());
        }
    }

    @SneakyThrows
    public UploadMediaRsp uploadMedia(File file, String fileType, String agentId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String boundary = LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_MULTIPART_PREFIX + System.currentTimeMillis();
        HttpRequest.BodyPublisher bodyPublisher = ofMimeMultipartData(file, boundary, fileType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(MessageFormat.format(UPLOAD_URL, accessToken(agentId).getAccessToken(), fileType)))
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_MULTIPART + boundary)
                .POST(bodyPublisher)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).readValue(response.body(), UploadMediaRsp.class);
    }

    @SneakyThrows
    private static HttpRequest.BodyPublisher ofMimeMultipartData(File file, String boundary, String fileType) {
        var builder = MultipartEntityBuilder.create();
        builder.setBoundary(boundary);
        builder.addBinaryBody(FORM_NAME, file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        var multipart = builder.build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        multipart.writeTo(baos);
        return HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray());
    }

    @SneakyThrows
    public void getMediaAndSave(String filePath, String filename, String filesuffix, String mediaId, String agentId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(GET_MEDIA_URL, accessToken(agentId).getAccessToken(), mediaId)))
                .GET()
                .build();
        HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(filePath + filename + filesuffix)));
    }
}
