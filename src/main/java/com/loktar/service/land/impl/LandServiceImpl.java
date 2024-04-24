package com.loktar.service.land.impl;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.land.Land;
import com.loktar.dto.land.LandDTO;
import com.loktar.dto.land.LandResultDTO;
import com.loktar.mapper.land.LandMapper;
import com.loktar.service.land.LandService;
import com.loktar.util.DateTimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LandServiceImpl implements LandService {

    private final LandMapper landMapper;

    private static final Map<String, String> STATUS_MAP = new HashMap<>();

    private final static String URL_DETAIL = "http://land.zzhz.zjol.com.cn/land/{0}.html";

    private final static String URK_LIST = "http://land.zzhz.zjol.com.cn/lands_data_list?year={0}";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public LandServiceImpl(LandMapper landMapper) {
        this.landMapper = landMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
        STATUS_MAP.put("39", "已成交");
        STATUS_MAP.put("40", "未成交");
        STATUS_MAP.put("41", "流拍");
        STATUS_MAP.put("42", "拍卖转挂牌");
        STATUS_MAP.put("43", "延期");
        STATUS_MAP.put("46", "终止");
    }

    @Override
    public void updateLandData(String year) {
        LocalDateTime firstDayOfYear = LocalDateTime.now().withDayOfYear(1);
        firstDayOfYear = firstDayOfYear.withYear(Integer.parseInt(year));
        String yearfirstDate = DateTimeUtil.getDatetimeStr(firstDayOfYear, DateTimeUtil.FORMATTER_DATE);
        int num = landMapper.deleteByDate(yearfirstDate);
        System.out.println(year + "年土拍数据开始删除，共" + num + "条");
        List<Land> lands = getData(year);
        landMapper.insertBatch(lands);
        System.out.println(year + "年土拍数据更新完成，共" + lands.size() + "条");
    }

    @SneakyThrows
    private List<Land> getData(String year) {
        List<Land> lands = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URK_LIST, year)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        LandResultDTO landResultDTO = objectMapper.readValue(response.body(), LandResultDTO.class);
        for (LandDTO landDTO : landResultDTO.getData()) {
            Land land = changeLandDTO(landDTO);
            lands.add(land);
        }
        return lands;
    }

    private Land changeLandDTO(LandDTO landDTO) {
        Land land = new Land();
        land.setId(StringUtils.isEmpty(landDTO.getId()) ? 0 : Integer.parseInt(landDTO.getId()));
        land.setDate(DateTimeUtil.parseLocalDate(landDTO.getYuEndTime(), DateTimeUtil.FORMATTER_DATE));
        land.setCity(landDTO.getCityName());
        land.setArea(landDTO.getUrbanName());
        land.setLandNo(landDTO.getNum());
        land.setLandName(landDTO.getName());
        land.setStatus(STATUS_MAP.get(landDTO.getPaystatusId()));
        land.setAcreage(StringUtils.isEmpty(landDTO.getTArea()) ? 0 : Float.parseFloat(landDTO.getTArea()));
        land.setLandUsage(landDTO.getPlanName());
        land.setVolumetricRate(landDTO.getFar());
        land.setDealPrice(StringUtils.isEmpty(landDTO.getPayPrice()) ? 0 : Float.parseFloat(landDTO.getPayPrice()));
        land.setBuildPrice(StringUtils.isEmpty(landDTO.getBuildPrice()) ? 0 : Float.parseFloat(landDTO.getBuildPrice()));
        land.setPremiumRate(landDTO.getPremiumRatio());
        land.setOwner(landDTO.getOwner());
        land.setRemark(landDTO.getNewMemo());
        land.setDetailUrl(MessageFormat.format(URL_DETAIL, landDTO.getId()));
        return land;
    }

}
