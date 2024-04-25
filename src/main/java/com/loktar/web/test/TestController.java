package com.loktar.web.test;

import com.loktar.conf.LokTarConfig;
import com.loktar.conf.LokTarConstant;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("test")
public class TestController {

    private final LokTarConfig lokTarConfig;

    private final Environment environment;

    public TestController(LokTarConfig lokTarConfig, Environment environment) {

        this.lokTarConfig = lokTarConfig;
        this.environment = environment;
    }

    @GetMapping("/test.do")
    public void test() {
        System.out.println(lokTarConfig.getTransmission().getUrl());
        boolean ss = Arrays.asList(environment.getActiveProfiles()).contains(LokTarConstant.ENV_PRO);
        System.out.println(ss);
    }

}
