package com.loktar.web.land;



import lombok.extern.slf4j.Slf4j;
import com.loktar.service.land.LandService;
import com.loktar.util.DateTimeUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("land")
@Slf4j
public class LandController {
    private final LandService landService;

    public LandController(LandService landService) {
        this.landService = landService;
    }

    @GetMapping("/update")
    public void update() {
        String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
        log.info("{}", year);
        landService.updateLandData(year);
    }
}
