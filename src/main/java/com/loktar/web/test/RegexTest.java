package com.loktar.web.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
    public static void main(String[] args) {
//        String str = "wsslc_detail_yfyj?id=3302e1515a1740beb912ea858e551cf7&yszbh=2018000009&zrzid=66151";
//        Pattern r = Pattern.compile("yszbh=([^&]+)&zrzid=([^&]+)");
//        Matcher m = r.matcher(str);
//        if (m.find()) {
//            System.out.println(m.group(0)); // 打印整个匹配到的字符串
//            System.out.println(m.group(1)); // 打印第一个括号内匹配到的字符串
//            System.out.println(m.group(2)); // 打印第二个括号内匹配到的字符串
//        } else {
//            System.out.println("No match found");
//        }
        String str = "Perfect.Singer.2024.S01E03.WEB-DL.1080p.H264.AAC-CMCTV";
        Pattern r = Pattern.compile( "(?!.*\\[FROZEN\\])(?!.*720).*CMCT$|(?!.*\\[FROZEN\\])(?=.*(?:2023|2024)).*CMCTV$");
        Matcher m = r.matcher(str);
        if (m.matches()) {
            System.out.println(1);
        }else{
            System.out.println(0);
        }




    }
}
