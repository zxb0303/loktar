package com.loktar.service.newhouse.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.common.Property;
import com.loktar.domain.newhouse.NewHouseHangzhouDetail;
import com.loktar.domain.newhouse.NewHouseHangzhouPresell;
import com.loktar.domain.newhouse.NewHouseHangzhouV2;
import com.loktar.dto.newhouse.NewHouseHangzhouPresellResultDTO;
import com.loktar.mapper.common.PropertyMapper;
import com.loktar.mapper.newhouse.NewHouseHangzhouDetailMapper;
import com.loktar.mapper.newhouse.NewHouseHangzhouPresellMapper;
import com.loktar.mapper.newhouse.NewHouseHangzhouV2Mapper;
import com.loktar.service.newhouse.NewHouseHangzhouV2Service;
import com.loktar.util.DelayUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class NewHouseHangzhouServiceV2Impl implements NewHouseHangzhouV2Service {

    private final NewHouseHangzhouV2Mapper newHouseHangzhouV2Mapper;

    private final PropertyMapper propertyMapper;

    private final NewHouseHangzhouPresellMapper newHouseHangzhouPresellMapper;

    private final NewHouseHangzhouDetailMapper newHouseHangzhouDetailMapper;

    private Property property;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public final static String URL_HOUSE_INFO = "https://www.tmsf.com/newhouse/property_{0}_{1}_basicinfo.htm";
    public final static String URL_PRESELL = "https://www.tmsf.com/newhouse/property_{0}_{1}_price.htm";
    public final static String URL_PRESELL_DETAIL = "https://www.tmsf.com/newhouse/NewPropertyHz_createPresellInfo.jspx?presellid={0}&sid={1}&propertyid={2}";
    public final static String URL_PRICE = "https://www.tmsf.com/newhouse/property_{0}_{1}_price.htm?presellid={2}&page={3}";

    //TODO 加了图形验证，待调整
    public NewHouseHangzhouServiceV2Impl(NewHouseHangzhouV2Mapper newHouseHangzhouV2Mapper, PropertyMapper propertyMapper, NewHouseHangzhouPresellMapper newHouseHangzhouPresellMapper, NewHouseHangzhouDetailMapper newHouseHangzhouDetailMapper) {
        this.newHouseHangzhouV2Mapper = newHouseHangzhouV2Mapper;
        this.propertyMapper = propertyMapper;
        this.newHouseHangzhouPresellMapper = newHouseHangzhouPresellMapper;
        this.newHouseHangzhouDetailMapper = newHouseHangzhouDetailMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void getNewHouseData(String areaCode, String houseId) {
        property = propertyMapper.selectByPrimaryKey("new_house_hangzhou");
        NewHouseHangzhouV2 newHouseHangzhouV2 = getHouse(areaCode, houseId);
        getPresell(newHouseHangzhouV2);
        List<NewHouseHangzhouPresell> newHouseHangzhouPresells = newHouseHangzhouPresellMapper.getPresellByHouseIdAndStatus(houseId, 0);
        for (NewHouseHangzhouPresell newHouseHangzhouPresell : newHouseHangzhouPresells) {
            getHouseDetail(newHouseHangzhouV2, newHouseHangzhouPresell);
        }
        int price = newHouseHangzhouDetailMapper.getAvgPrice(houseId);
        newHouseHangzhouV2.setPrice(price);
        newHouseHangzhouV2Mapper.updateByPrimaryKey(newHouseHangzhouV2);
    }

    @SneakyThrows
    private void getHouseDetail(NewHouseHangzhouV2 newHouseHangzhouV2, NewHouseHangzhouPresell newHouseHangzhouPresell) {
        List<NewHouseHangzhouDetail> newHouseHangzhouDetails = new ArrayList<>();
        newHouseHangzhouDetailMapper.deleteByHouseIdAndPresellId(newHouseHangzhouV2.getHouseId(), newHouseHangzhouPresell.getPresellId());
        int page = 1;
        int maxPage = 1;
        while (page <= maxPage) {
            DelayUtil.delaySeconds(1,3);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MessageFormat.format(URL_PRICE, newHouseHangzhouV2.getAreaCode(), newHouseHangzhouV2.getHouseId(), newHouseHangzhouPresell.getPresellId(), String.valueOf(page))))
                    .timeout(Duration.ofSeconds(10))
                    .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                    .header("Cookie", property.getValue())
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            Document document = Jsoup.parse(response.body());
            if (page == 1) {
                Element pageDiv = document.selectFirst("[class=spagenext]");
                String str = pageDiv.selectFirst("span").html();
                String str2 = str.split("总数")[0];
                maxPage = Integer.valueOf(str2.split("/")[1].trim());
            }
            List<Element> trs = document.selectFirst("[class=onbuildshow]").selectFirst("[class=onbuildshow_contant colordg ft14]").selectFirst("[class=sjtd]").select("tr");
            for (Element tr : trs) {
                List<Element> tds = tr.select("td");
                NewHouseHangzhouDetail newHouseHangzhouDetail = new NewHouseHangzhouDetail();
                newHouseHangzhouDetail.setDetailId(UUID.randomUUID().toString());
                newHouseHangzhouDetail.setHouseId(newHouseHangzhouV2.getHouseId());
                newHouseHangzhouDetail.setPresellId(newHouseHangzhouPresell.getPresellId());
                String buildAndUnitStr = tds.get(0).select("a").html();
                String[] buildAndUnitStrArr = buildAndUnitStr.split("幢");
                if (buildAndUnitStrArr.length >= 1) {
                    newHouseHangzhouDetail.setBuildNo(buildAndUnitStrArr[0]);
                }
                if (buildAndUnitStrArr.length == 2) {
                    newHouseHangzhouDetail.setUnitNo(buildAndUnitStrArr[1].replace("单元", ""));
                }
                newHouseHangzhouDetail.setRoomNo(tds.get(1).select("a").select("div").html().replace("室", ""));
                newHouseHangzhouDetail.setBuildArea(changeNum(tds.get(2).select("a").select("div").html()));
                newHouseHangzhouDetail.setInnerArea(changeNum(tds.get(3).select("a").select("div").html()));
                newHouseHangzhouDetail.setAreaRate(changeNum(tds.get(4).select("a").select("div").html()));
                newHouseHangzhouDetail.setRecordPrice(changeNum(tds.get(5).select("a").select("div").html()));
                newHouseHangzhouDetail.setFixPrice(changeNum(tds.get(6).select("a").select("div").html()));
                newHouseHangzhouDetail.setPrice(changeNum(tds.get(7).select("a").select("div").html()));
                String status = tds.get(8).select("a").html();
                if (ObjectUtils.isEmpty(status)) {
                    status = tds.get(8).select("font").html();
                }
                newHouseHangzhouDetail.setStatus(status);
                newHouseHangzhouDetails.add(newHouseHangzhouDetail);
            }
            page = page + 1;
            //如果没抓到数据，就再来一次
            if (trs.size() == 0) {
                Thread.sleep(2000);
                page = page - 1;
            }
        }
        for (NewHouseHangzhouDetail newHouseHangzhouDetail : newHouseHangzhouDetails) {
            newHouseHangzhouDetailMapper.insert(newHouseHangzhouDetail);
        }
        newHouseHangzhouPresell.setUpdateStatus(1);
        newHouseHangzhouPresellMapper.updateByPrimaryKey(newHouseHangzhouPresell);
    }

    @SneakyThrows
    private void getPresell(NewHouseHangzhouV2 newHouseHangzhouV2) {
        DelayUtil.delaySeconds(1,3);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_PRESELL, newHouseHangzhouV2.getAreaCode(), newHouseHangzhouV2.getHouseId())))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                .header("Cookie", property.getValue())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Document document = Jsoup.parse(response.body());
        List<Element> elements = document.getElementById("presell_dd").select("a");
        for (Element e : elements) {
            if (e.html().equals("全部")) {
                continue;
            }
            NewHouseHangzhouPresell newHouseHangzhouPresell = new NewHouseHangzhouPresell();
            newHouseHangzhouPresell.setPresellId(e.id().split("_")[1]);
            newHouseHangzhouPresell.setHouseId(newHouseHangzhouV2.getHouseId());
            newHouseHangzhouPresell.setPresellNo(e.html());
            newHouseHangzhouPresell = getPresellDetail(newHouseHangzhouV2, newHouseHangzhouPresell);
            if (ObjectUtils.isEmpty(newHouseHangzhouPresellMapper.selectByPrimaryKey(newHouseHangzhouPresell.getPresellId()))) {
                newHouseHangzhouPresell.setUpdateStatus(0);
                newHouseHangzhouPresellMapper.insert(newHouseHangzhouPresell);
            } else {
                newHouseHangzhouPresell.setUpdateStatus(1);
                newHouseHangzhouPresellMapper.updateByPrimaryKey(newHouseHangzhouPresell);
            }
        }
    }

    private NewHouseHangzhouPresell getPresellDetail(NewHouseHangzhouV2 newHouseHangzhouV2, NewHouseHangzhouPresell newHouseHangzhouPresell) throws Exception {
        DelayUtil.delaySeconds(1,3);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_PRESELL_DETAIL, newHouseHangzhouPresell.getPresellId(), newHouseHangzhouV2.getAreaCode(), newHouseHangzhouV2.getHouseId())))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .header("Cookie", property.getValue())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        //TODO 打印
        System.out.println(response.body());
        NewHouseHangzhouPresellResultDTO newHouseHangzhouPresellResultDTO = objectMapper.readValue(response.body(), NewHouseHangzhouPresellResultDTO.class);
        if (ObjectUtils.isEmpty(newHouseHangzhouV2.getNameSpread()) && !ObjectUtils.isEmpty(newHouseHangzhouPresellResultDTO.getPre())) {
            newHouseHangzhouV2.setNameSpread(newHouseHangzhouPresellResultDTO.getPre().getPropertyname());
            newHouseHangzhouV2.setArea(newHouseHangzhouPresellResultDTO.getPre().getDistrictname());
            newHouseHangzhouV2.setPlate(newHouseHangzhouPresellResultDTO.getPre().getAreaname());
            newHouseHangzhouV2Mapper.updateByPrimaryKey(newHouseHangzhouV2);
        }
        newHouseHangzhouPresell.setPrice(newHouseHangzhouPresellResultDTO.getPresell().getOpeningprice());
        newHouseHangzhouPresell.setDate(newHouseHangzhouPresellResultDTO.getPresell().getApplydate());
        if (newHouseHangzhouPresellResultDTO.getPre() == null) {
            newHouseHangzhouPresell.setTotalHouseNum(0);
            newHouseHangzhouPresell.setSoldHouseNum(0);
            newHouseHangzhouPresell.setLimitHouseNum(0);
        } else {
            newHouseHangzhouPresell.setTotalHouseNum(newHouseHangzhouPresellResultDTO.getPre().getNum());
            newHouseHangzhouPresell.setSoldHouseNum(newHouseHangzhouPresellResultDTO.getPre().getDealnum());
            newHouseHangzhouPresell.setLimitHouseNum(newHouseHangzhouPresellResultDTO.getPre().getLimitnum());
        }
        return newHouseHangzhouPresell;
    }

    @SneakyThrows
    private NewHouseHangzhouV2 getHouse(String areaCode, String houseId) {
        NewHouseHangzhouV2 newHouseHangzhouV2 = new NewHouseHangzhouV2();
        DelayUtil.delaySeconds(1,3);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_HOUSE_INFO, areaCode, houseId)))
                .timeout(Duration.ofSeconds(10))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_HTML)
                .header("Cookie", property.getValue())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Document document = Jsoup.parse(response.body());
        Element nameDiv = document.selectFirst("[class=house_title_name]");
        List<Element> divs = document.select("[class=house_essential_list_mian]");
        Element typeDiv = divs.get(2).selectFirst("[class=house_essential_list_right]");
        Element plotRatioDiv = divs.get(4).selectFirst("[class=house_essential_list_right]");
        Element greenRatioDiv = divs.get(5).selectFirst("[class=house_essential_list_right]");
        Element coverAreaDiv = divs.get(6).selectFirst("[class=house_essential_list_right]");
        Element bulidAreaDiv = divs.get(8).selectFirst("[class=house_essential_list_right]");
        Element bulidTypeDiv = divs.get(3).selectFirst("[class=house_essential_list_right]");
        Element totalHouseNumDiv = divs.get(9).selectFirst("[class=house_essential_list_right]");
        Element carParkNumDiv = divs.get(11).selectFirst("[class=house_essential_list_right]");
        List<Element> divs2 = document.select("[class=house_essential_list_all]");
        Element addressDiv = divs2.get(0).selectFirst("[class=house_essential_list_right]");
        newHouseHangzhouV2.setHouseId(houseId);
        newHouseHangzhouV2.setName(nameDiv.html());
        newHouseHangzhouV2.setPrice(0);
        newHouseHangzhouV2.setType(typeDiv.html());
        String plotRadioStr = plotRatioDiv.html();
        Double plotRadio = 0d;
        if (StringUtils.isNumeric(plotRadioStr)) {
            plotRadio = Double.valueOf(plotRadioStr);
        }
        newHouseHangzhouV2.setPlotRatio(plotRadio);
        newHouseHangzhouV2.setGreenRatio(greenRatioDiv.html());
        newHouseHangzhouV2.setCoverArea(coverAreaDiv.html());
        newHouseHangzhouV2.setBulidArea(bulidAreaDiv.html());
        newHouseHangzhouV2.setBulidType(bulidTypeDiv.html());
        if (StringUtils.isNumeric(carParkNumDiv.html())) {
            newHouseHangzhouV2.setCarParkNum(Integer.valueOf(carParkNumDiv.html()));
        } else {
            newHouseHangzhouV2.setCarParkNum(0);
        }
        newHouseHangzhouV2.setAreaCode(areaCode);
        newHouseHangzhouV2.setAddress(addressDiv.html());
        newHouseHangzhouV2.setUpdateTime(new Date());
        if (ObjectUtils.isEmpty(newHouseHangzhouV2Mapper.selectByPrimaryKey(houseId))) {
            newHouseHangzhouV2Mapper.insert(newHouseHangzhouV2);
        } else {
            newHouseHangzhouV2Mapper.updateByPrimaryKey(newHouseHangzhouV2);
        }
        return newHouseHangzhouV2;
    }

    public String changeNum(String str) {
        str = str.replace("<span class=\"numberone\"></span>", "2");
        str = str.replace("<span class=\"numbbone\"></span>", "1");
        str = str.replace("<span class=\"numbertwo\"></span>", "3");
        str = str.replace("<span class=\"numbbtwo\"></span>", "2");
        str = str.replace("<span class=\"numberthree\"></span>", "4");
        str = str.replace("<span class=\"numbbthree\"></span>", "3");
        str = str.replace("<span class=\"numberfour\"></span>", "5");
        str = str.replace("<span class=\"numbbfour\"></span>", "4");
        str = str.replace("<span class=\"numberfive\"></span>", "1");
        str = str.replace("<span class=\"numbbfive\"></span>", "5");
        str = str.replace("<span class=\"numbersix\"></span>", "6");
        str = str.replace("<span class=\"numbbsix\"></span>", "6");
        str = str.replace("<span class=\"numberseven\"></span>", "7");
        str = str.replace("<span class=\"numbbseven\"></span>", "7");
        str = str.replace("<span class=\"numbereight\"></span>", "8");
        str = str.replace("<span class=\"numbbeight\"></span>", "8");
        str = str.replace("<span class=\"numbernine\"></span>", "9");
        str = str.replace("<span class=\"numbbnine\"></span>", "9");
        str = str.replace("<span class=\"numberzero\"></span>", "0");
        str = str.replace("<span class=\"numbbzero\"></span>", "0");
        str = str.replace("<span class=\"numberdor\"></span>", ".");
        str = str.replace("元/㎡", "");
        str = str.replace("㎡", "");
        str = str.replace("元", "");
        str = str.replace("　", "");
        return str.trim();
    }

}
