package com.loktar.learn.jdk8;

public class Lambda {
    public static void main(String[] args) {
        //1.1 使用匿名内部类
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world 1.1");
            }
        }).start();
        //1.2 使用lambda表达式
        new Thread(()->System.out.println("hello world 1.2")).start();
        //1.3 使用lambda表达式及类型推导
        new Thread(()->{
            System.out.println("hello world 1.3");
        }).start();
    }
}
