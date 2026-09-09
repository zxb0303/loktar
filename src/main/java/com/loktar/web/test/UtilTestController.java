package com.loktar.web.test;

import com.loktar.util.IPUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("utiltest")
@Slf4j
public class UtilTestController {

    private final IPUtil ipUtil;

    public UtilTestController(IPUtil ipUtil) {
        this.ipUtil = ipUtil;
    }

    @GetMapping("/iputil")
    @SneakyThrows
    public String iputil() {
        return ipUtil.getip();
    }

}
