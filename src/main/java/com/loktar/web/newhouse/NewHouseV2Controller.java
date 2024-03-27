package com.loktar.web.newhouse;


import com.loktar.service.newhouse.NewHouseHangzhouV2Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("newhouseV2")
public class NewHouseV2Controller {

    private final NewHouseHangzhouV2Service newHouseHangzhouV2Service;

    public NewHouseV2Controller(NewHouseHangzhouV2Service newHouseHangzhouV2Service) {
        this.newHouseHangzhouV2Service = newHouseHangzhouV2Service;
    }


    @RequestMapping("/getNewHouseData.do")
    public String getNewHouseData(String ids) {
        if (ids.indexOf("_") == -1) {
            return "ids不对。应该是areaCode_houseId";
        }
        String[] strs = ids.split("_");
        String areaCode = strs[0];
        String houseId = strs[1];
        newHouseHangzhouV2Service.getNewHouseData(areaCode, houseId);
        return "完成";
    }
}
