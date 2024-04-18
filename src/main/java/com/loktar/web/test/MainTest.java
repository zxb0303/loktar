package com.loktar.web.test;

import com.loktar.util.DateTimeUtil;

import java.time.LocalDateTime;

public class MainTest {

    public static void main(String[] args) {
        String yestodayStr = DateTimeUtil.getDatetimeStr(LocalDateTime.now().minusDays(1), DateTimeUtil.FORMATTER_DATE);

        System.out.println(yestodayStr);


    }

}

