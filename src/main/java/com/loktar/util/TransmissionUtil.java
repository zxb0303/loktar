package com.loktar.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import com.loktar.dto.transmission.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransmissionUtil {
    public final static String TRANSMISSION_SESSION_ID = "X-Transmission-Session-Id";
    private final static long TRANSMISSION_SESSION_ID_EXPIRE = 28 * 60;
    private final static String AUTHORIZATION = "Authorization";
    private final RedisUtil redisUtil;
    private final LokTarConfig lokTarConfig;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public TransmissionUtil(RedisUtil redisUtil, LokTarConfig lokTarConfig) {
        this.redisUtil = redisUtil;
        this.lokTarConfig = lokTarConfig;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @SneakyThrows
    private TrResponse rpc(TrRequest trRequest) {
        String sessionId = (String) redisUtil.get(TRANSMISSION_SESSION_ID);
        if (StringUtils.isEmpty(sessionId)) {
            sessionId = "";
        }
        HttpClient httpClient = HttpClient.newHttpClient();
        String requestStr = objectMapper.writeValueAsString(trRequest);
        //TODO 打印
        //System.out.println(requestStr);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(lokTarConfig.transmissionUrl))
                .timeout(Duration.ofSeconds(60))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .header(AUTHORIZATION, lokTarConfig.transmissionAuthorization)
                .header(TRANSMISSION_SESSION_ID, sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(requestStr))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 409) {
            sessionId = response.headers().firstValue(TRANSMISSION_SESSION_ID).orElse(null);
            redisUtil.set(TRANSMISSION_SESSION_ID, sessionId, TRANSMISSION_SESSION_ID_EXPIRE);
            return rpc(trRequest);
        }
        //TODO 打印
        //System.out.println(response.body());
        TrResponse trResponse = objectMapper.readValue(response.body(), TrResponse.class);
        //TODO 打印
        //System.out.println(trResponse.toString());
        return trResponse;
    }

    public TrResponse addTorrent(String url, String downloadDir, boolean paused) {
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.TORRENT_ADD.getName());
        TrRequestArg trRequestArg = new TrRequestArg();
        trRequestArg.setFilename(url);
        trRequestArg.setPaused(paused);
        trRequestArg.setDownloadDir(downloadDir);
        trRequest.setArguments(trRequestArg);
        return rpc(trRequest);
    }

    public TrResponse startTorrents(Integer[] ids) {
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.TORRENT_START.getName());
        TrRequestArg trRequestArg = new TrRequestArg();
        trRequestArg.setIds(ids);
        trRequest.setArguments(trRequestArg);
        return rpc(trRequest);
    }


    public TrResponse removeTorrents(Integer[] ids, boolean deleteLocalData) {
        if (ids.length == 0) {
            System.out.println("不传id会全删除的 朋友");
            return null;
        }
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.TORRENT_REMOVE.getName());
        TrRequestArg trRequestArg = new TrRequestArg();
        trRequestArg.setIds(ids);
        trRequestArg.setDeleteLocalData(deleteLocalData);
        trRequest.setArguments(trRequestArg);
        return rpc(trRequest);
    }

    public TrResponse getTorrents(Integer[] ids) {
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.TORRENT_GET.getName());
        TrRequestArg trRequestArg = new TrRequestArg();
        if (!ObjectUtils.isEmpty(ids)) {
            trRequestArg.setIds(ids);
        }
        trRequestArg.setFields(getFieldNames(TrResponseTorrent.class));
        trRequest.setArguments(trRequestArg);
        return rpc(trRequest);
    }

    public TrResponse getAllTorrents() {
        return getTorrents(null);
    }

    public TrResponse getFreeSpaceByPath(String path) {
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.FREE_SPACE.getName());
        TrRequestArg trRequestArg = new TrRequestArg();
        trRequestArg.setPath(path);
        trRequest.setArguments(trRequestArg);
        return rpc(trRequest);
    }

    //true是限速 false是不限速
    public TrResponse altSpeedEnabled(boolean enabled) {
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.SESSION_SET.getName());
        TrRequestArg trRequestArg = new TrRequestArg();
        trRequestArg.setAltSpeedEnabled(enabled);
        trRequest.setArguments(trRequestArg);
        return rpc(trRequest);
    }

    public TrResponse getSession() {
        TrRequest trRequest = new TrRequest();
        trRequest.setMethod(TrRequestMethodType.SESSION_GET.getName());
        return rpc(trRequest);
    }

    public static String[] getFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        // 循环处理当前类及其父类
        while (clazz != null && clazz != Object.class) {
            // 获取当前类的所有声明的字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 将字段名添加到列表中
                fieldNames.add(field.getName());
            }
            // 移动到父类进行下一轮处理
            clazz = clazz.getSuperclass();
        }
        // 将列表转换为String数组并返回
        return fieldNames.toArray(new String[0]);
    }


}
