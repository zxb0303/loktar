package com.loktar.util;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DelayUtil {
    /**
     * 延迟minSecond ~ maxSecond秒
     * @param minSecond
     * @param maxSecond
     */
    public static void delaySeconds(int minSecond,int maxSecond){
        Random random = new Random();
        int millis = random.nextInt((maxSecond-minSecond)*1000)+minSecond*1000;
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
