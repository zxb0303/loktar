package com.loktar.web.newhouse;



import lombok.extern.slf4j.Slf4j;
import com.loktar.domain.newhouse.NewHouseHangzhouV3;
import com.loktar.domain.newhouse.NewHouseHangzhouV3Detail;
import com.loktar.domain.newhouse.NewHouseHangzhouV3Presell;
import com.loktar.domain.newhouse.NewHouseHangzhouV3PresellBuild;
import com.loktar.service.newhouse.NewHouseHangzhouV3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("newhouseV3")
@Slf4j
public class NewHouseV3Controller {

    private final NewHouseHangzhouV3Service newHouseHangzhouV3Service;

    public NewHouseV3Controller(NewHouseHangzhouV3Service newHouseHangzhouV3Service) {
        this.newHouseHangzhouV3Service = newHouseHangzhouV3Service;
    }



    @GetMapping("/memberLogin")
    public void memberLogin() {
        newHouseHangzhouV3Service.memberLogin();
    }

    @GetMapping("/getNewHouseDataByName")
    public void getNewHouseData(String houseName) {
        NewHouseHangzhouV3 newHouseHangzhouV3 = newHouseHangzhouV3Service.getNewHouseData(houseName);
    }

    @GetMapping("/getNewHousePresellDataByHouseId")
    public void getNewHousePresellDataByHouseId(String houseId) {
        log.info("{}", houseId);
        List<NewHouseHangzhouV3Presell> newHouseHangzhouV3 = newHouseHangzhouV3Service.getNewHousePresellDataByHouseId(houseId);
    }

    @GetMapping("/getNewHousePresellBuildDataByHouseId")
    public void getNewHousePresellBuildDataByHouseId(String houseId) {
        List<NewHouseHangzhouV3PresellBuild> newHouseHangzhouV3PresellBuilds = newHouseHangzhouV3Service.getNewHousePresellBuildDataByHouseId(houseId);
    }
    @GetMapping("/getNewHouseDetailByHouseId")
    public void getNewHouseDetailByHouseId(String houseId) {
        List<NewHouseHangzhouV3Detail> newHouseHangzhouV3Details = newHouseHangzhouV3Service.getNewHouseDetailByHouseId(houseId);
    }

    @GetMapping("/getNewHouseDetailByHouseIdAndAzure")
    public void coverImages(String houseId) {
        newHouseHangzhouV3Service.getNewHouseDetailByHouseIdAndAzure(houseId);
    }


}
