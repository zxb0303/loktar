package com.loktar.service.second.impl;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.domain.second.SecondHandHouse;
import com.loktar.dto.second.SecondHandHouseDTO;
import com.loktar.dto.second.SecondHandHouseResultDTO;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.mapper.second.SecondHandHouseMapper;
import com.loktar.service.second.SecondHandHouseService;
import com.loktar.util.DateTimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SecondHandHouseServiceImpl implements SecondHandHouseService {

    private final SecondHandHouseMapper secondHandHouseMapper;

    private final PropertyMapper propertyMapper;

    private final static String URL = "https://zwfw.fgj.hangzhou.gov.cn/jjhygl/webty/WebFyAction_getGpxxSelectList.jspx";

    private final static String REQUEST_STR = "gply=1&starttime={0}&endtime={1}&page={2}&xqid=0";

    public SecondHandHouseServiceImpl(SecondHandHouseMapper secondHandHouseMapper, PropertyMapper propertyMapper) {
        this.secondHandHouseMapper = secondHandHouseMapper;
        this.propertyMapper = propertyMapper;
    }

    /**
     * @description: 更新二手房数据
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-05-07 13:09
     */
    @Override
    public void updateSecondHandHouseData() {
        Property property = propertyMapper.selectByPrimaryKey("second_hand_house");
        String dateStr = secondHandHouseMapper.getMaxDate();
        LocalDate date = DateTimeUtil.parseLocalDate(dateStr, DateTimeUtil.FORMATTER_DATE).plusDays(1);
        dateStr = DateTimeUtil.getDatetimeStr(date, DateTimeUtil.FORMATTER_DATE);
        LocalDate yestoday = LocalDate.now().minusDays(1);
        while(date.isBefore(yestoday)){
            List<SecondHandHouse> secondHandHouses = getHouseData(dateStr, property);
            System.out.println(dateStr + "共" + secondHandHouses.size() + "条数据待处理");
            if (!secondHandHouses.isEmpty()) {
                secondHandHouseMapper.insertBatch(secondHandHouses);
            }
            System.out.println(dateStr + "共" + secondHandHouses.size() + "条数据处理完成");
            date=date.plusDays(1);
            dateStr = DateTimeUtil.getDatetimeStr(date, DateTimeUtil.FORMATTER_DATE);
        }






    }

    /**
     * @description: 根据日期获取挂牌数据
     * @param: date
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-05-07 15:05
     */
    @SneakyThrows
    private List<SecondHandHouse> getHouseData(String date, Property property) {
        List<SecondHandHouse> secondHandHouses = new ArrayList<>();
        int totalNum = 1;
        int pageName = 1;
        while (secondHandHouses.size() < totalNum) {
            Thread.sleep(500);
            HttpClient httpClient = HttpClient.newHttpClient();
            String requestBody = MessageFormat.format(REQUEST_STR, date, date, String.valueOf(pageName));
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(30))
                    .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                    .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_FORM)
                    .header(LokTarConstant.HTTP_HEADER_COOKIE_NAME, property.getValue())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                SecondHandHouseResultDTO secondHandHouseResultDTO = objectMapper.readValue(response.body(), SecondHandHouseResultDTO.class);
                if (secondHandHouseResultDTO.getList() == null) {
                    System.out.println("cookie失效");
                    throw new Exception("cookie失效");
                }
                List<SecondHandHouseDTO> secondHandHouseDTOS = secondHandHouseResultDTO.getList();
                for (SecondHandHouseDTO secondHandHouseDTO : secondHandHouseDTOS) {
                    SecondHandHouse secondHandHouse = changeDTO(secondHandHouseDTO);
                    secondHandHouses.add(secondHandHouse);
                }
                totalNum = secondHandHouseResultDTO.getTotaltows();
                //TODO 打印
                System.out.println("完成 " + date + "," + secondHandHouses.size() + "/" + totalNum);
                pageName = pageName + 1;
            }
        }
        return secondHandHouses;
    }

    /**
     * @description: 转换DTO
     * @param: secondHandHouseDTO
     * @retuan: lookup.fun.domain.second.SecondHandHouse
     * @author: zxb
     * @createTime: 2021-05-08 11:04
     */
    private SecondHandHouse changeDTO(SecondHandHouseDTO secondHandHouseDTO) {
        SecondHandHouse secondHandHouse = new SecondHandHouse();
//        secondHandHouse.setId(secondHandHouseDTO.getGpfyid());
        secondHandHouse.setFwtybh(secondHandHouseDTO.getFwtybh());
        secondHandHouse.setXzqhname(secondHandHouseDTO.getXzqhname());
        if (ObjectUtils.isEmpty(secondHandHouse.getXzqhname())) {
            secondHandHouse.setXzqhname("杭州");
        }
        secondHandHouse.setCqmc(secondHandHouseDTO.getCqmc());
        secondHandHouse.setXqmc(secondHandHouseDTO.getXqmc());
        secondHandHouse.setJzmj(secondHandHouseDTO.getJzmj());
        secondHandHouse.setWtcsjg(String.valueOf(secondHandHouseDTO.getWtcsjg()));
        secondHandHouse.setMdmc(secondHandHouseDTO.getMdmc());
        secondHandHouse.setGplxrxm(secondHandHouseDTO.getGplxrxm());
        secondHandHouse.setScgpshsj(secondHandHouseDTO.getScgpshsj());
        secondHandHouse.setStatus("挂牌");
        secondHandHouse.setStatusTime(LocalDateTime.now());
        return secondHandHouse;
    }
}
