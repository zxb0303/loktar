package com.loktar.util;

import java.util.Random;

public class DelayUtil {
    /**
     * 延迟minSecond ~ maxSecond秒
     * @param minSecond
     * @param maxSecond
     */
    public static void delaySeconds(int minSecond,int maxSecond){
        Random random = new Random();
        int second = random.nextInt((maxSecond-minSecond)*1000)+minSecond*1000;
        try {
            Thread.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
