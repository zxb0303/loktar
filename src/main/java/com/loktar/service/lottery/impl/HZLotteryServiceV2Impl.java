package com.loktar.service.lottery.impl;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loktar.conf.LokTarConstant;
import com.loktar.domain.lottery.LotteryHouse;
import com.loktar.domain.lottery.LotteryOtherPeople;
import com.loktar.domain.lottery.LotteryPeople;
import com.loktar.dto.lottery.HZLotteryHouseDTO;
import com.loktar.dto.lottery.HZLotteryHouseDetailResultDTO;
import com.loktar.dto.lottery.HZLotteryHouseResultDTO;
import com.loktar.dto.lottery.HZLotteryPeopleDTOV2;
import com.loktar.mapper.lottery.LotteryHouseMapper;
import com.loktar.mapper.lottery.LotteryOtherPeopleMapper;
import com.loktar.mapper.lottery.LotteryPeopleMapper;
import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.HZnotaryUtil;
import com.loktar.util.PDFUtilForLotteryHouse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.ObjectUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class HZLotteryServiceV2Impl implements HZLotteryServiceV2 {

    private final LotteryHouseMapper lotteryHouseMapper;

    private final LotteryPeopleMapper lotteryPeopleMapper;

    private final LotteryOtherPeopleMapper lotteryOtherPeopleMapper;

    private final static String URL_HOUSE_DETAIL = "https://miniprogram.hz-notary.com/app/api/houseDetail?houses.id={0}";

    private final static String URL_HOUSE_LIST = "https://miniprogram.hz-notary.com/app/api/lottery?layPage.pageNum=1&layPage.pageSize=20";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Value("${conf.pdf.path}")
    private String pdfPath;

    public HZLotteryServiceV2Impl(LotteryHouseMapper lotteryHouseMapper, LotteryPeopleMapper lotteryPeopleMapper, LotteryOtherPeopleMapper lotteryOtherPeopleMapper) {
        this.lotteryHouseMapper = lotteryHouseMapper;
        this.lotteryPeopleMapper = lotteryPeopleMapper;
        this.lotteryOtherPeopleMapper = lotteryOtherPeopleMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * @description: 更新市区摇号数据
     * @param:
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-04-30 12:50
     */
    @Override
    public void updateHZLotteryData() {
        //获取杭州市摇号网站近20条楼盘清单
        List<LotteryHouse> recentHZLotteryHouses = getRecentHZLotteryHouses();
        //新增或者更新到数据库中
        insertOrUpdateHZLotteryHouses(recentHZLotteryHouses);
        //查询数据库近x日杭州已摇号楼盘
        List<LotteryHouse> lotteryHouses = lotteryHouseMapper.selectLotteryedHZHousesByDay(10);
        //更新已摇号楼盘人员数据
        updateLotteryPeoples(lotteryHouses);
    }

    /**
     * @description: 更新新摇号楼盘人员数据
     * @param: lotteryHouses
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-05-06 12:01
     */
    public void updateLotteryPeoples(List<LotteryHouse> lotteryHouses) {
        for (LotteryHouse lotteryHouse : lotteryHouses) {
            int MaxRank = lotteryHouseMapper.getMaxRankByHouseId(lotteryHouse.getHouseId());
            if (MaxRank < lotteryHouse.getTotalPeopleNum()) {
                System.out.println(lotteryHouse.getHouseName() + " 摇号人员数据需要更新");
                System.out.println("清空历史数据-其他人员");
                lotteryOtherPeopleMapper.deleteLotteryOtherPeoplesByHouseId(lotteryHouse.getHouseId());
                System.out.println("清空历史数据-人员");
                lotteryPeopleMapper.deleteLotteryPeopleByHouseId(lotteryHouse.getHouseId());
                System.out.println("开始获取人员数据");
                getLotteryPeopleData(lotteryHouse);
                System.out.println(lotteryHouse.getHouseName() + " 摇号人员数据完成更新");
            }
        }
    }

    /**
     * @description: 获取摇号人员数据
     * @param: lotteryHouse
     * @retuan: java.util.List<lookup.fun.domain.lottery.LotteryPeople>
     * @author: zxb
     * @createTime: 2021-05-06 12:56
     */
    private void getLotteryPeopleData(LotteryHouse lotteryHouse) {
        List<LotteryPeople> lotteryPeoples = new ArrayList<LotteryPeople>();
        List<LotteryOtherPeople> lotteryOtherPeoples = new ArrayList<LotteryOtherPeople>();
        String pdfUrl = HZnotaryUtil.getPDFUrlByHouseNameAndType(lotteryHouse.getHouseName(), HZnotaryUtil.TYPE_REGIST);
        List<HZLotteryPeopleDTOV2> hZLotteryPeopleDTOV2s = PDFUtilForLotteryHouse.getTableContentFromPDFUrl(pdfUrl, pdfPath);
        String pdfUrl2 = HZnotaryUtil.getPDFUrlByHouseNameAndType(lotteryHouse.getHouseName(), HZnotaryUtil.TYPE_RESULT);
        Map<String, Integer> rankMap = PDFUtilForLotteryHouse.getTextContentFromPDFUrl(pdfUrl2, pdfPath);
        for (HZLotteryPeopleDTOV2 hZLotteryPeopleDTOV2 : hZLotteryPeopleDTOV2s) {
            LotteryPeople lotteryPeople = new LotteryPeople();
            lotteryPeople.setHouseId(lotteryHouse.getHouseId());
            lotteryPeople.setPeopleId(UUID.randomUUID().toString());
            lotteryPeople.setSerialNum(hZLotteryPeopleDTOV2.getSerialNum());
            lotteryPeople.setName(hZLotteryPeopleDTOV2.getName());
            lotteryPeople.setIdentityNum(hZLotteryPeopleDTOV2.getIdentityNum());
            lotteryPeople.setFamilyType(hZLotteryPeopleDTOV2.getFamilyType());
            lotteryPeople.setHasOtherPeople(hZLotteryPeopleDTOV2.getHasOtherPeople());
            lotteryPeople.setLotteryRank(rankMap.get(lotteryPeople.getSerialNum()));
            if (hZLotteryPeopleDTOV2.getHasOtherPeople() == 1) {
                String[] otherpeopleNames = (hZLotteryPeopleDTOV2.getOtherBuyersName().replace("，", ",")).split(",");
                String[] otherpeopleIds = (hZLotteryPeopleDTOV2.getOtherBuyersIdnumber().replace("，", ",")).split(",");
                for (int j = 0; j < otherpeopleNames.length; j++) {
                    LotteryOtherPeople lotteryOtherPeople = new LotteryOtherPeople();
                    lotteryOtherPeople.setOtherPeopleId(UUID.randomUUID().toString());
                    lotteryOtherPeople.setHouseId(lotteryHouse.getHouseId());
                    lotteryOtherPeople.setPeopleId(lotteryPeople.getPeopleId());
                    lotteryOtherPeople.setName(otherpeopleNames[j]);
                    if (j < otherpeopleIds.length) {
                        lotteryOtherPeople.setIdentityNum(otherpeopleIds[j]);
                    }
                    lotteryOtherPeoples.add(lotteryOtherPeople);
                }
            }
            lotteryPeoples.add(lotteryPeople);
        }
        lotteryPeopleMapper.insertBatch(lotteryPeoples);
        lotteryOtherPeopleMapper.insertBatch(lotteryOtherPeoples);

        //TODO 打印
        System.out.println("人数:" + lotteryPeoples.size());
    }

    /**
     * @description: 新增或更新近期市区摇号数据
     * @param: recentHZLotteryHouses
     * @retuan: void
     * @author: zxb
     * @createTime: 2021-04-30 13:26
     */
    private void insertOrUpdateHZLotteryHouses(List<LotteryHouse> recentHZLotteryHouses) {
        for (LotteryHouse lotteryHouse : recentHZLotteryHouses) {
            lotteryHouse = getHZlotteryHouseDetailByHouseId(lotteryHouse.getHouseId());
            LotteryHouse exist = lotteryHouseMapper.selectByPrimaryKey(lotteryHouse.getHouseId());
            if (ObjectUtils.isEmpty(exist)) {
                lotteryHouseMapper.insert(lotteryHouse);
                System.out.println("新增摇号楼盘：" + lotteryHouse.getHouseName() + "(" + lotteryHouse.getLotteryTime() + ")");
            } else {
                lotteryHouseMapper.updateByPrimaryKey(lotteryHouse);
                System.out.println("更新摇号楼盘：" + lotteryHouse.getHouseName() + "(" + lotteryHouse.getLotteryTime() + ")");
            }
        }

    }

    /**
     * @description: 获取摇号楼盘详细数据
     * @param: lotteryHouseId
     * @retuan: lookup.fun.domain.lottery.LotteryHouse
     * @author: zxb
     * @createTime: 2021-04-30 14:15
     */
    @SneakyThrows
    private LotteryHouse getHZlotteryHouseDetailByHouseId(String lotteryHouseId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(URL_HOUSE_DETAIL, lotteryHouseId)))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        HZLotteryHouseDetailResultDTO hZLotteryHouseDetailResultDTO = objectMapper.readValue(response.body(), HZLotteryHouseDetailResultDTO.class);
        LotteryHouse lotteryHouse = changeLotteryHouseDTO(hZLotteryHouseDetailResultDTO.getData(), true);
        return lotteryHouse;
    }

    /**
     * @description: 获取近期市区摇号数据
     * @param:
     * @retuan: java.util.List<lookup.fun.domain.lottery.LotteryHouse>
     * @author: zxb
     * @createTime: 2021-04-30 13:23
     */
    @SneakyThrows
    private List<LotteryHouse> getRecentHZLotteryHouses() {
        List<LotteryHouse> lotteryHouses = new ArrayList<LotteryHouse>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_HOUSE_LIST))
                .timeout(Duration.ofSeconds(30))
                .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        HZLotteryHouseResultDTO hZLotteryHouseResultDTO = objectMapper.readValue(response.body(), HZLotteryHouseResultDTO.class);
        for (HZLotteryHouseDTO hZLotteryHouseDTO : hZLotteryHouseResultDTO.getDataList()) {
            LotteryHouse lotteryHouse = changeLotteryHouseDTO(hZLotteryHouseDTO, false);
            lotteryHouses.add(lotteryHouse);
        }
        return lotteryHouses;
    }

    /**
     * @description: 转换DTO
     * @param: hZLotteryHouseDTO
     * @param: isAllData 是否所有数据都转换，只有detail数据转换才需要
     * @retuan: lookup.fun.domain.lottery.LotteryHouse
     * @author: zxb
     * @createTime: 2021-04-30 17:13
     */
    private LotteryHouse changeLotteryHouseDTO(HZLotteryHouseDTO hZLotteryHouseDTO, boolean isAllData) {
        LotteryHouse lotteryHouse = new LotteryHouse();
        lotteryHouse.setHouseId(hZLotteryHouseDTO.getId());
        lotteryHouse.setHouseName(hZLotteryHouseDTO.getHouseName());
        lotteryHouse.setLotteryTime(hZLotteryHouseDTO.getLotteryTime());
        lotteryHouse.setStatus(hZLotteryHouseDTO.getStatus());
        if (!isAllData) {
            return lotteryHouse;
        }
        lotteryHouse.setTotalPeopleNum(hZLotteryHouseDTO.getTotal_people());
        lotteryHouse.setTotalHouseNum(hZLotteryHouseDTO.getHouse_number());
        lotteryHouse.setElitePeopleNum(hZLotteryHouseDTO.getElite_people());
        lotteryHouse.setEliteHouseNum(hZLotteryHouseDTO.getElite_room());
        lotteryHouse.setHomelessPeopleNum(hZLotteryHouseDTO.getHomeless_people());
        lotteryHouse.setHomelessHouseNum(hZLotteryHouseDTO.getHomeless_number());
        lotteryHouse.setUnhomelessPeopleNum(lotteryHouse.getTotalPeopleNum() - lotteryHouse.getElitePeopleNum() - lotteryHouse.getHomelessPeopleNum());
        lotteryHouse.setUnhomelessHouseNum(lotteryHouse.getTotalHouseNum() - lotteryHouse.getEliteHouseNum() - lotteryHouse.getHomelessHouseNum());
        double unluckyElitePeopleNum = lotteryHouse.getElitePeopleNum() - lotteryHouse.getEliteHouseNum();
        double extraHomelessHouseNum = 0;
        if (unluckyElitePeopleNum <= 0) {
            unluckyElitePeopleNum = 0;
            extraHomelessHouseNum = lotteryHouse.getEliteHouseNum() - lotteryHouse.getElitePeopleNum();
        }
        double realHomelessHouseNum = lotteryHouse.getHomelessHouseNum() + extraHomelessHouseNum;
        double unluckyHomelessPeopleNum = lotteryHouse.getHomelessPeopleNum() + unluckyElitePeopleNum - lotteryHouse.getHomelessHouseNum();
        double extraUnHomelessHouseNum = 0;
        if (unluckyHomelessPeopleNum <= 0) {
            unluckyHomelessPeopleNum = 0;
            extraUnHomelessHouseNum = lotteryHouse.getHomelessHouseNum() - lotteryHouse.getHomelessPeopleNum() - unluckyElitePeopleNum;
        }
        double realUnHomelessHouseNum = lotteryHouse.getUnhomelessHouseNum() + extraUnHomelessHouseNum;
        double secondRoundPeopleNum = lotteryHouse.getHomelessPeopleNum() + unluckyElitePeopleNum;
        double thirdRoundPeopleNum = lotteryHouse.getUnhomelessPeopleNum() + unluckyHomelessPeopleNum;
        double elitechance = lotteryHouse.getEliteHouseNum() * 1.0 / lotteryHouse.getElitePeopleNum() * 1.0
                + realHomelessHouseNum * 1.0 / secondRoundPeopleNum * 1.0 * unluckyElitePeopleNum / secondRoundPeopleNum * 1.0
                + realUnHomelessHouseNum * 1.0 / thirdRoundPeopleNum * 1.0 * unluckyElitePeopleNum * 1.0 / thirdRoundPeopleNum * 1.0;
        double homelesschance = realHomelessHouseNum * 1.0 / secondRoundPeopleNum * 1.0 * lotteryHouse.getHomelessPeopleNum() / secondRoundPeopleNum * 1.0
                + realUnHomelessHouseNum * 1.0 / thirdRoundPeopleNum * 1.0 * unluckyHomelessPeopleNum * 1.0 / thirdRoundPeopleNum * 1.0;
        double unhomelesschance = realUnHomelessHouseNum * 1.0 / thirdRoundPeopleNum * 1.0 * lotteryHouse.getUnhomelessPeopleNum() * 1.0 / thirdRoundPeopleNum * 1.0;
        String elitechanceStr = elitechance >= 1 ? "100%" : String.format("%.2f", elitechance * 100) + "%";
        String homelesschanceStr = homelesschance >= 1 ? "100%" : String.format("%.2f", homelesschance * 100) + "%";
        String unhomelesschanceStr = unhomelesschance >= 1 ? "100%" : String.format("%.2f", unhomelesschance * 100) + "%";
        lotteryHouse.setEliteChance(elitechanceStr);
        lotteryHouse.setHomelessChance(homelesschanceStr);
        lotteryHouse.setUnhomelessChance(unhomelesschanceStr);
        return lotteryHouse;
    }

}
