package com.loktar.learn.jdk21;


import lombok.extern.slf4j.Slf4j;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VirtualThread {
    @SneakyThrows
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("{}", "Hello, virtual thread11!");

        });
        // 也可以指定虚拟线程的名字
        Thread thread2 = Thread.ofVirtual().name("virtual thread").start(() -> log.info("{}", "Hello, virtual thread22!"));
        threads.add(thread2);
        threads.add(thread1);
        for (Thread thread : threads){
            thread.join();
        }
        log.info("{}", "Hello, main thread33!");
    }
}
