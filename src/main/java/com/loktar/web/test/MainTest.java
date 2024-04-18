package com.loktar.web.test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MainTest {

    public static void main(String[] args) {
        // 假设这是从JSON解析得到的ZonedDateTime实例
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2024-04-17T08:42:55Z");
        // 使用系统默认时区进行转换
        ZonedDateTime localZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        // 转换为LocalDateTime
        LocalDateTime localDateTime = localZonedDateTime.toLocalDateTime();

        // 输出结果
        System.out.println("ZonedDateTime: " + zonedDateTime);
        System.out.println("LocalDateTime: " + localDateTime);


    }

}

