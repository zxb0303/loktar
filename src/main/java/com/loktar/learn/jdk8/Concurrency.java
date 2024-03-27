package com.loktar.learn.jdk8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Concurrency {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        // 使用CompletableFuture进行异步计算
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            // 模拟耗时操作
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            // 模拟耗时操作
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 100;
        });

        // 使用CompletableFuture处理异步计算结果
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
        combinedFuture.thenRun(() -> {
            int result1 = future1.join();
            int result2 = future2.join();
            map.put("result1", result1);
            map.put("result2", result2);
            System.out.println("Results: " + map);
        });
        // 等待所有任务完成
        combinedFuture.join();
        System.out.println("111");
    }
}
