package com.loktar.web.land;


import com.loktar.service.land.LandService;
import com.loktar.util.DateTimeUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("land")
public class LandController {
    private final LandService landService;

    public LandController(LandService landService) {
        this.landService = landService;
    }

    @GetMapping("/update.do")
    public void update() {
        String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
        System.out.println(year);
        landService.updateLandData(year);
    }
}
