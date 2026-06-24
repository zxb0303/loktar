package com.loktar.web.second;



import lombok.extern.slf4j.Slf4j;
import com.loktar.mapper.second.SecondHandHouseMapper;
import com.loktar.service.second.SecondHandHouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("second")
@Slf4j
public class SecondController {
    private final SecondHandHouseService secondHandHouseService;

    private final SecondHandHouseMapper secondHandHouseMapper;


    public SecondController(SecondHandHouseService secondHandHouseService, SecondHandHouseMapper secondHandHouseMapper) {
        this.secondHandHouseService = secondHandHouseService;
        this.secondHandHouseMapper = secondHandHouseMapper;
    }

    @GetMapping("/updateSecondHandHouseData.do")
    public void updateSecondHandHouseData() {
        log.info("{}", "开始手动更新二手房数据");
        secondHandHouseService.updateSecondHandHouseData();
        log.info("{}", "结束手动更新二手房数据");
    }

}
