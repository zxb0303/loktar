package com.loktar.util.wx.qywx;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.qywx.QywxMenu;
import com.loktar.dto.wx.*;
import com.loktar.dto.wx.agentmsg.*;
import com.loktar.mapper.qywx.QywxMenuMapper;
import com.loktar.util.DateTimeUtil;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class QywxApi {
    private final static String KEY_ACCESSTOKEN = "qywx_accessToken_";

    private final static String ACCESSTOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={0}&corpsecret={1}";

    private final static String SEND_AGENTMSG_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={0}";

    private final static String MENU_CREATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token={0}&agentid={1}";

    private final static String UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token={0}&type=file";

    private final static String GET_MEDIA_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token={0}&media_id={1}";

    private final static String FORM_NAME = "media";

    private final RedisUtil redisUtil;

    private final QywxMenuMapper qywxMenuMapper;

    private final LokTarConfig lokTarConfig;

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static Map<String, String> AGENTMAP = new HashMap<>();


    public QywxApi(RedisUtil redisUtil, QywxMenuMapper qywxMenuMapper, LokTarConfig lokTarConfig) {
        this.redisUtil = redisUtil;
        this.qywxMenuMapper = qywxMenuMapper;
        this.lokTarConfig = lokTarConfig;
        AGENTMAP.put(lokTarConfig.qywxAgent002Id, lokTarConfig.qywxAgent002Secert);
        AGENTMAP.put(lokTarConfig.qywxAgent003Id, lokTarConfig.qywxAgent003Secert);
        AGENTMAP.put(lokTarConfig.qywxAgent004Id, lokTarConfig.qywxAgent004Secert);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 获取accessToken
     */
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
        String secret = AGENTMAP.get(agentId);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(ACCESSTOKEN_URL, lokTarConfig.qywxCorpId, secret)))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        AccessToken accessToken = objectMapper.readValue(response.body(), AccessToken.class);
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
        String requestbody = objectMapper.writeValueAsString(message);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(SEND_AGENTMSG_URL, accessToken(agentId).getAccessToken())))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(requestbody))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), AgentMsgRsp.class);

    }

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
        String requestBody = objectMapper.writeValueAsString(wxMenu);
        System.out.println(requestBody);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(MENU_CREATE_URL, accessToken(agentId).getAccessToken(), agentId)))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return objectMapper.readValue(response.body(), AgentMsgRsp.class);
    }

    private Menu.Button getButton(QywxMenu config) {
        KeyButtonType type = KeyButtonType.getByName((config.getType()));
        switch (type) {
            case KeyButtonType.VIEW:
                return new Menu.ViewButton(config.getName(), config.getUrl(), config.getOrder());
            default:
                return new Menu.KeyButton(type, config.getName(), config.getKey(),config.getOrder());
        }
    }

    @SneakyThrows
    public UploadMediaRsp uploadMedia(File file, String agentId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String boundary = LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_MULTIPART_PREFIX + System.currentTimeMillis();
        HttpRequest.BodyPublisher bodyPublisher = ofMimeMultipartData(file, boundary);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(MessageFormat.format(UPLOAD_URL, accessToken(agentId).getAccessToken())))
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_MULTIPART + boundary)
                .POST(bodyPublisher)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), UploadMediaRsp.class);
    }

    @SneakyThrows
    private static HttpRequest.BodyPublisher ofMimeMultipartData(File file, String boundary) {
        var builder = MultipartEntityBuilder.create();
        builder.setBoundary(boundary);
        builder.addBinaryBody(FORM_NAME, file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        var multipart = builder.build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        multipart.writeTo(baos);
        return HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray());
    }

    @SneakyThrows
    public String saveMedia(String filePath, String mediaId, String agentId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(GET_MEDIA_URL, accessToken(agentId).getAccessToken(), mediaId)))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        // 从响应头获取文件名
        String filename = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_FILENAME) + LokTarConstant.VOICE_SUFFIX_AMR;
        Path destination = Paths.get(filePath + filename);
        // 将响应体写入到文件中，并确保如果文件已存在则覆盖
        Files.write(destination, response.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return filename;
    }

//    private static String getFileName(HttpResponse<?> response) {
//        return response.headers().firstValue("Content-disposition")
//                .map(header -> header.split(";"))
//                .flatMap(parts -> {
//                    for (String part : parts) {
//                        if (part.trim().startsWith("filename=")) {
//                            return Optional.of(part.split("=")[1].replaceAll("\"", ""));
//                        }
//                    }
//                    return Optional.empty();
//                }).orElse(DateUtil.DATEFORMATMINUTESECONDSTR);
//    }

}
