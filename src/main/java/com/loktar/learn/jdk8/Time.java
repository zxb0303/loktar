package com.loktar.learn.jdk8;


import lombok.extern.slf4j.Slf4j;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
public class Time {
    public static void main(String[] args) {
        // 创建日期
        LocalDate date = LocalDate.now();
        log.info("{}", "Today's date: " + date);

        // 创建时间
        LocalTime time = LocalTime.now();
        log.info("{}", "Current time: " + time);

        // 创建日期和时间
        LocalDateTime dateTime = LocalDateTime.now();
        log.info("{}", "Current date and time: " + dateTime);

        // 格式化日期和时间
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("{}", "Formatted date and time: " + formattedDateTime);

        // 执行日期的计算操作
        LocalDate tomorrow = date.plusDays(1);
        log.info("{}", "Tomorrow's date: " + tomorrow);

        // 执行时间的比较操作
        boolean isBefore = time.isBefore(LocalTime.NOON);
        log.info("{}", "Is current time before noon? " + isBefore);
    }
}
