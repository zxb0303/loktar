package com.loktar.web.second;


import com.loktar.service.second.SecondHandHouseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("second")
public class SecondController {
    private final SecondHandHouseService secondHandHouseService;

    public SecondController(SecondHandHouseService secondHandHouseService) {
        this.secondHandHouseService = secondHandHouseService;
    }

    @RequestMapping("/update.do")
    public void update() throws Exception {
        System.out.println("开始手动更新二手房数据");
//        secondHandHouseService.updateSecondHandHouseStatus();
        System.out.println("结束手动更新二手房数据");
    }

    @RequestMapping("/needUpdate.do")
    public void needUpdate() {
//        SecondTask.needUpdate = true;
    }

    @RequestMapping("/updateSecondHandHouseData.do")
    public void updateSecondHandHouseData() {
        System.out.println("开始手动更新二手房数据");
        secondHandHouseService.updateSecondHandHouseData();
        System.out.println("结束手动更新二手房数据");
    }
}
