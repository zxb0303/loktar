package com.loktar.web.land;


import com.loktar.service.land.LandService;
import com.loktar.util.DateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("land")
public class LandController {
    private final LandService landService;

    public LandController(LandService landService) {
        this.landService = landService;
    }

    @RequestMapping("/update.do")
    public void update() {
        String year = DateUtil.format(new Date(), DateUtil.DATEFORMATYEAR);
        System.out.println(year);
        landService.updateLandData(year);
    }
}
