package com.loktar.util;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loktar.conf.LokTarConstant;
import com.loktar.conf.LokTarPrivateConstant;
import com.loktar.dto.transmission.TrRequestArgDTO;
import com.loktar.dto.transmission.TrRequestDTO;
import com.loktar.dto.transmission.TrResponseDTO;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TransmissionUtil {
    //TODO 待修改
    //TODO 待正式发布前修改


    public static String TEMP_DOWNLOAD_DIR="/downloads2/complete";
    public static String SESSION_ID = "-1";
    public static String[] FIELDS = new String[]{"id", "name", "status", "totalSize", "downloadDir", "error", "errorString", "uploadRatio", "percentDone", "peersSendingToUs", "peersGettingFromUs", "rateUpload", "addedDate", "activityDate", "doneDate", "trackerStats", "hashString"};

    public static TrResponseDTO addTorrent(String url, String downloadDir, boolean paused) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode obj = objectMapper.createObjectNode();
        ObjectNode arguments = objectMapper.createObjectNode();
        obj.put("method", "torrent-add");
        arguments.put("filename", url);
        arguments.put("paused", paused);
        arguments.put("download-dir", downloadDir);
        obj.put("arguments", arguments);
        String requestStr = obj.toString();
        return trPRC(requestStr);
    }

    public static TrResponseDTO startTorrents(Integer[] ids) {
        TrRequestDTO trRequestDTO = new TrRequestDTO();
        trRequestDTO.setMethod("torrent-start");
        TrRequestArgDTO trRequestArgDTO = new TrRequestArgDTO();
        trRequestArgDTO.setIds(ids);
        trRequestDTO.setArguments(trRequestArgDTO);
        return trPRC(trRequestDTO);
    }


    public static TrResponseDTO removeTorrents(Integer[] ids, boolean deleteLocalData) {
        if (ids.length == 0) {
            System.out.println("不传id会全删除的 朋友");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode obj = objectMapper.createObjectNode();
        ObjectNode arguments = objectMapper.createObjectNode();
        ArrayNode idsArray = objectMapper.createArrayNode();
        for (Integer id : ids) {
            idsArray.add(id);
        }
        obj.put("method", "torrent-remove");
        arguments.put("delete-local-data", deleteLocalData);
        arguments.put("ids", idsArray);
        obj.put("arguments", arguments);
        String requestStr = obj.toString();
        return trPRC(requestStr);
    }

    public static TrResponseDTO getAllTorrents() {
        TrRequestDTO trRequestDTO = new TrRequestDTO();
        trRequestDTO.setMethod("torrent-get");
        TrRequestArgDTO trRequestArgDTO = new TrRequestArgDTO();
        trRequestArgDTO.setFields(FIELDS);
        trRequestDTO.setArguments(trRequestArgDTO);
        return trPRC(trRequestDTO);
    }

    public static TrResponseDTO getTorrents(Integer[] ids) {
        TrRequestDTO trRequestDTO = new TrRequestDTO();
        trRequestDTO.setMethod("torrent-get");
        TrRequestArgDTO trRequestArgDTO = new TrRequestArgDTO();
        trRequestArgDTO.setIds(ids);
        trRequestArgDTO.setFields(FIELDS);
        trRequestDTO.setArguments(trRequestArgDTO);
        return trPRC(trRequestDTO);
    }

    public static TrResponseDTO getRecentlyActiveTorrents() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode obj = objectMapper.createObjectNode();
        ObjectNode arguments = objectMapper.createObjectNode();
        ArrayNode fieldsArray = objectMapper.createArrayNode();
        for (String field : FIELDS) {
            fieldsArray.add(field);
        }
        obj.put("method", "torrent-get");
        arguments.put("ids", "recently-active");
        arguments.put("fields", fieldsArray);
        obj.put("arguments", arguments);
        String requestStr = obj.toString();
        return trPRC(requestStr);
    }

    public static TrResponseDTO getFreeSpaceByPath(String path) {
        TrRequestDTO trRequestDTO = new TrRequestDTO();
        trRequestDTO.setMethod("free-space");
        TrRequestArgDTO trRequestArgDTO = new TrRequestArgDTO();
        trRequestArgDTO.setPath(path);
        trRequestDTO.setArguments(trRequestArgDTO);
        return trPRC(trRequestDTO);
    }

    //true是限速 false是不限速
    public static TrResponseDTO altSpeedEnabled(boolean enabled) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode obj = objectMapper.createObjectNode();
        ObjectNode arguments = objectMapper.createObjectNode();
        obj.put("method", "session-set");
        arguments.put("alt-speed-enabled", enabled);
        obj.put("arguments", arguments);
        String requestStr = obj.toString();
        return trPRC(requestStr);
    }

    public static TrResponseDTO getSession() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode obj = objectMapper.createObjectNode();
        ObjectNode arguments = objectMapper.createObjectNode();
        obj.put("method", "session-get");
        obj.put("arguments", arguments);
        String requestStr = obj.toString();
        return trPRC(requestStr);
    }


    @SneakyThrows
    public static TrResponseDTO trPRC(TrRequestDTO trRequestDTO) {
        return trPRC(new ObjectMapper().writeValueAsString(trRequestDTO));
    }

    public static TrResponseDTO trPRC(String requestStr) {
        int retry = 5;
        TrResponseDTO trResponseDTO = null;
        while (ObjectUtils.isEmpty(trResponseDTO) && retry >= 1) {
            trResponseDTO = realTrPRC(requestStr);
        }
        return trResponseDTO;
    }

    @SneakyThrows
    public static TrResponseDTO realTrPRC(String requestStr) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(LokTarPrivateConstant.TRANSMISSION_URL))
                .timeout(Duration.ofSeconds(60))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN)
                .header("Authorization", "Basic emhhb3hpYW9iaW46dHJhbnNtaXNzaW9uMTIzWnhi")
                .header("X-Transmission-Session-Id", SESSION_ID)
                .POST(HttpRequest.BodyPublishers.ofString(requestStr))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            TrResponseDTO trResponseDTO = objectMapper.readValue(response.body(), TrResponseDTO.class);
            return trResponseDTO;
        }
        String html = response.body().replace("&nbsp;", " ");
        Document document = Jsoup.parse(html);
        Element code = document.selectFirst("code");
        SESSION_ID = code.html().trim().replace("X-Transmission-Session-Id:", "");
        System.out.println("X-Transmission-Session-Id过期,新SessionId为：" + SESSION_ID);
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getRecentlyActiveTorrents());
    }
}
