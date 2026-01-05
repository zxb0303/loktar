package com.loktar.web.test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class MainTest {
    private final static String HOST = "a.vv.com";



    public static void main(String[] args) {

        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        // 当月第一天 00:00:00 UTC
        ZonedDateTime startUtc = nowUtc.withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneOffset.UTC);
        // EndTime：当前日期减去2天的 23:59:59 UTC
        ZonedDateTime endUtc = nowUtc.minusDays(2)
                .toLocalDate()
                .atTime(23, 59, 59)
                .atZone(ZoneOffset.UTC);

        String endTime = String.valueOf(endUtc.toEpochSecond());

        System.out.println(endTime);

        LocalDate date = LocalDate.parse("2025-12-28");
        long start1 = date.withDayOfMonth(1) // 调整为当月1号
                .atStartOfDay(ZoneOffset.UTC) // 设置时间为 00:00:00 并指定 UTC
                .toEpochSecond(); // 转为秒级时间戳
        long end1 = date.atTime(23, 59, 59) // 设置时间为 23:59:59
                .toInstant(ZoneOffset.UTC) // 指定 UTC
                .getEpochSecond();
        long start2 = date.atStartOfDay(ZoneOffset.UTC)
                .toEpochSecond();
        System.out.println(start1);
        System.out.println(end1);
        System.out.println(start2);
    }

}

