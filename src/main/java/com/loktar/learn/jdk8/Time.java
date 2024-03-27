package com.loktar.learn.jdk8;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Time {
    public static void main(String[] args) {
        // 创建日期
        LocalDate date = LocalDate.now();
        System.out.println("Today's date: " + date);

        // 创建时间
        LocalTime time = LocalTime.now();
        System.out.println("Current time: " + time);

        // 创建日期和时间
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println("Current date and time: " + dateTime);

        // 格式化日期和时间
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Formatted date and time: " + formattedDateTime);

        // 执行日期的计算操作
        LocalDate tomorrow = date.plusDays(1);
        System.out.println("Tomorrow's date: " + tomorrow);

        // 执行时间的比较操作
        boolean isBefore = time.isBefore(LocalTime.NOON);
        System.out.println("Is current time before noon? " + isBefore);
    }
}
