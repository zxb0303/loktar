package com.loktar.web.test;

import com.loktar.mapper.cxy.EmployeeMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.loktar.learn.test.*;

@RestController
@RequestMapping("test")
public class TestController {

    private final EmployeeMapper employeeMapper;

    private final Config config;

    public TestController(EmployeeMapper employeeMapper, Config config) {
        this.employeeMapper = employeeMapper;
        this.config = config;
    }

    @RequestMapping("/test.do")
    public void test() {

    }


    @RequestMapping("/test1.do")
    public void test1() {
        String str = config.ip;
        System.out.println(str);
    }
}
