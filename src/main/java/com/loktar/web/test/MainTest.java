package com.loktar.web.test;

import com.loktar.util.DateTimeUtil;

import java.time.LocalDate;

public class MainTest {

    public static void main(String[] args) {
        String str ="2023-04-01";
        LocalDate date = DateTimeUtil.parseLocalDate(str, DateTimeUtil.FORMATTER_DATE).plusDays(1);
        System.out.println("LocalDateTime: " + date);
    }

}

