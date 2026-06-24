package com.loktar.learn.jdk8;


import lombok.extern.slf4j.Slf4j;
@Slf4j
public class Lambda {
    public static void main(String[] args) {
        //1.1 使用匿名内部类
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("{}", "hello world 1.1");
            }
        }).start();
        //1.2 使用lambda表达式
        new Thread(()->log.info("{}", "hello world 1.2")).start();
        //1.3 使用lambda表达式及类型推导
        new Thread(()->{
            log.info("{}", "hello world 1.3");
        }).start();
    }
}
