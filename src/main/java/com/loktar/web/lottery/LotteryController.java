package com.loktar.web.lottery;


import com.loktar.conf.LokTarConfig;
import com.loktar.domain.lottery.LotteryHouse;
import com.loktar.dto.lottery.HZLotteryPeopleDTOV2;
import com.loktar.mapper.lottery.LotteryHouseMapper;
import com.loktar.service.lottery.HZLotteryServiceV2;
import com.loktar.util.PDFUtilForLotteryHouse;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final LokTarConfig lokTarConfig;

    public LotteryController(HZLotteryServiceV2 hZLotteryServiceV2, LotteryHouseMapper lotteryHouseMapper, LokTarConfig lokTarConfig) {
        this.hZLotteryServiceV2 = hZLotteryServiceV2;
        this.lotteryHouseMapper = lotteryHouseMapper;
        this.lokTarConfig = lokTarConfig;
    }


    @GetMapping("/updateHZLotteryData.do")
    public void updateHZLotteryData() {
        hZLotteryServiceV2.updateHZLotteryData();
    }

    @GetMapping("/updateHZLotteryDataByHouseId.do")
    public void updateHZLotteryDataByHouseId(String houseId) {
        List<LotteryHouse> lotteryHouses = new ArrayList<>();
        LotteryHouse lotteryHouse = lotteryHouseMapper.selectByPrimaryKey(houseId);
        lotteryHouses.add(lotteryHouse);
        hZLotteryServiceV2.updateLotteryPeoples(lotteryHouses);
    }

    @GetMapping("/testTablePdf.do")
    public void test1() {
        String pdfUrl = "http://down.hz-notary.com:10006/pdf/2022/1126/221126082946440_37750059084723307.pdf";
        List<HZLotteryPeopleDTOV2> hZLotteryPeopleDTOV2s = PDFUtilForLotteryHouse.getTableContentFromPDFUrl(pdfUrl, lokTarConfig.getPath().getPdf());
        for (HZLotteryPeopleDTOV2 hZLotteryPeopleDTOV2 : hZLotteryPeopleDTOV2s) {
            System.out.println(hZLotteryPeopleDTOV2.getPeopleId() + ";" + hZLotteryPeopleDTOV2.getSerialNum() + ";" +
                    hZLotteryPeopleDTOV2.getName() + ";" +
                    hZLotteryPeopleDTOV2.getIdentityNum() + ";" +
                    hZLotteryPeopleDTOV2.getFamilyType() + ";" +
                    hZLotteryPeopleDTOV2.getOtherBuyersName() + ";" +
                    hZLotteryPeopleDTOV2.getOtherBuyersIdnumber() + ";");
        }
    }

    @GetMapping("/testTextPdf.do")
    public void test2() {
        String pdfUrl = "http://down.hz-notary.com:10006/pdf/2022/1127/221127095410081_37841523207888202.pdf";
        Map<String, Integer> map = PDFUtilForLotteryHouse.getTextContentFromPDFUrl(pdfUrl, lokTarConfig.getPath().getPdf());

    }
}
