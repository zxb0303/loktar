package com.loktar.web.second;


import com.loktar.domain.second.SecondHandHouse;
import com.loktar.mapper.second.SecondHandHouseMapper;
import com.loktar.service.second.SecondHandHouseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("second")
public class SecondController {
    private final SecondHandHouseService secondHandHouseService;

    private final SecondHandHouseMapper secondHandHouseMapper;


    public SecondController(SecondHandHouseService secondHandHouseService, SecondHandHouseMapper secondHandHouseMapper) {
        this.secondHandHouseService = secondHandHouseService;
        this.secondHandHouseMapper = secondHandHouseMapper;
    }

    @RequestMapping("/updateSecondHandHouseData.do")
    public void updateSecondHandHouseData() {
        System.out.println("开始手动更新二手房数据");
        secondHandHouseService.updateSecondHandHouseData();
        System.out.println("结束手动更新二手房数据");
    }

    @RequestMapping("/test.do")
    public void test() {
       List<SecondHandHouse> secondHandHouses = new ArrayList<>();
        SecondHandHouse secondHandHouse = new SecondHandHouse();
        secondHandHouse.setFwtybh("111");
        secondHandHouses.add(secondHandHouse);
        secondHandHouseMapper.insertBatch(secondHandHouses);
    }


}
