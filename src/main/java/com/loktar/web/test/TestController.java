package com.loktar.web.test;

import com.loktar.mapper.cxy.EmployeeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("test")
public class TestController {

    private final EmployeeMapper employeeMapper;

    public TestController(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @RequestMapping("/test.do")
    public void test2() {

    }


    @RequestMapping("/test1.do")
    public ResponseEntity<Void> test1() {
        CompletableFuture.runAsync(() -> {
            // 异步执行的代码
            System.out.println("123");
        });
            return ResponseEntity.noContent().build();
    }
}
