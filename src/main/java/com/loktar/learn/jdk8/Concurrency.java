package com.loktar.learn.jdk8;


import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Concurrency {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        // 使用CompletableFuture进行异步计算
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            // 模拟耗时操作
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 42;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            // 模拟耗时操作
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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
            log.info("{}", "Results: " + map);
        });
        // 等待所有任务完成
        combinedFuture.join();
        log.info("{}", "111");
    }
}
