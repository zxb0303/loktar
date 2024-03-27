package com.loktar.web.lottery;


import com.loktar.domain.lottery.LotteryHouse;
import com.loktar.dto.lottery.HZLotteryPeopleDTOV2;
import com.loktar.mapper.lottery.LotteryHouseMapper;
import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.PDFUtilForLotteryHouse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("lottery")
public class LotteryController {

    private final HZLotteryServiceV2 hZLotteryServiceV2;

    private final LotteryHouseMapper lotteryHouseMapper;

    public LotteryController(HZLotteryServiceV2 hZLotteryServiceV2, LotteryHouseMapper lotteryHouseMapper) {
        this.hZLotteryServiceV2 = hZLotteryServiceV2;
        this.lotteryHouseMapper = lotteryHouseMapper;
    }

//    private final YHLotteryService yHLotteryService;
//
//    @RequestMapping("/updateYHLotteryData.do")
//    public void updateYHLotteryData() {
//        yHLotteryService.updateYHLotteryData();
//    }

    @RequestMapping("/updateHZLotteryData.do")
    public void updateHZLotteryData() {
        hZLotteryServiceV2.updateHZLotteryData();
    }

    @RequestMapping("/updateHZLotteryDataByHouseId.do")
    public void updateHZLotteryDataByHouseId(String houseId) {
        List<LotteryHouse> lotteryHouses = new ArrayList<>();
        LotteryHouse lotteryHouse = lotteryHouseMapper.selectByPrimaryKey(houseId);
        lotteryHouses.add(lotteryHouse);
        hZLotteryServiceV2.updateLotteryPeoples(lotteryHouses);
    }

    @Value("${conf.pdf.path}")
    private String pdfPath;

    @RequestMapping("/test1.do")
    public void test1() {
        String pdfUrl = "http://down.hz-notary.com:10006/pdf/2022/1126/221126082946440_37750059084723307.pdf";
        List<HZLotteryPeopleDTOV2> hZLotteryPeopleDTOV2s = PDFUtilForLotteryHouse.getTableContentFromPDFUrl(pdfUrl, pdfPath);
        for (int i = 0; i < hZLotteryPeopleDTOV2s.size(); i++) {
            HZLotteryPeopleDTOV2 hZLotteryPeopleDTOV2 = hZLotteryPeopleDTOV2s.get(i);
            System.out.println(hZLotteryPeopleDTOV2.getPeopleId() + ";" + hZLotteryPeopleDTOV2.getSerialNum() + ";" +
                    hZLotteryPeopleDTOV2.getName() + ";" +
                    hZLotteryPeopleDTOV2.getIdentityNum() + ";" +
                    hZLotteryPeopleDTOV2.getFamilyType() + ";" +
                    hZLotteryPeopleDTOV2.getOtherBuyersName() + ";" +
                    hZLotteryPeopleDTOV2.getOtherBuyersIdnumber() + ";");
        }
    }

    @RequestMapping("/test2.do")
    public void test2() {
        String pdfUrl = "http://down.hz-notary.com:10006/pdf/2022/1127/221127095410081_37841523207888202.pdf";
        Map<String, Integer> map = PDFUtilForLotteryHouse.getTextContentFromPDFUrl(pdfUrl, pdfPath);

    }
}
