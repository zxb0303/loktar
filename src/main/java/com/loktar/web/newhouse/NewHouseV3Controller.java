package com.loktar.web.newhouse;


import com.loktar.domain.newhouse.NewHouseHangzhouV3;
import com.loktar.domain.newhouse.NewHouseHangzhouV3Presell;
import com.loktar.domain.newhouse.NewHouseHangzhouV3PresellBuild;
import com.loktar.service.newhouse.NewHouseHangzhouV3Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("newhouseV3")
public class NewHouseV3Controller {

    private final NewHouseHangzhouV3Service newHouseHangzhouV3Service;

    public NewHouseV3Controller(NewHouseHangzhouV3Service newHouseHangzhouV3Service) {
        this.newHouseHangzhouV3Service = newHouseHangzhouV3Service;
    }


    @RequestMapping("/getNewHouseDataByName.do")
    public void getNewHouseData(String houseName) {
        NewHouseHangzhouV3 newHouseHangzhouV3 = newHouseHangzhouV3Service.getNewHouseData(houseName);
    }

    @RequestMapping("/getNewHousePresellDataByHouseId.do")
    public void getNewHousePresellDataByHouseId(String houseId) {
        System.out.println(houseId);
        List<NewHouseHangzhouV3Presell> newHouseHangzhouV3 = newHouseHangzhouV3Service.getNewHousePresellDataByHouseId(houseId);
    }

    @RequestMapping("/getNewHousePresellBuildDataByHouseId.do")
    public void getNewHousePresellBuildDataByHouseId(String houseId) {
        List<NewHouseHangzhouV3PresellBuild> newHouseHangzhouV3PresellBuilds = newHouseHangzhouV3Service.getNewHousePresellBuildDataByHouseId(houseId);
    }

    @RequestMapping("/memberLogin.do")
    public void memberLogin() {
        newHouseHangzhouV3Service.memberLogin();
    }
}
