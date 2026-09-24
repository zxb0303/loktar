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
        int seconds = random.nextInt(maxSecond - minSecond) + minSecond;
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
