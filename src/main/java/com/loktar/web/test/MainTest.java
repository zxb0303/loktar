package com.loktar.web.test;

public class MainTest {
    private final static String HOST = "a.vv.com";



    public static void main(String[] args) {

       //获取HOST的域名
        String host = HOST.substring(HOST.indexOf(".") + 1);
        System.out.println(host);
    }

}

