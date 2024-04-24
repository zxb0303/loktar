package com.loktar.web.test;

import com.loktar.learn.test.Config;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private final Config config;

    public TestController( Config config) {
        this.config = config;
    }

    @GetMapping("/test.do")
    public void test() {
        System.out.println(1);
    }


    @GetMapping("/test1.do")
    public void test1() {
        String str = config.ip;
        System.out.println(str);
    }
}
