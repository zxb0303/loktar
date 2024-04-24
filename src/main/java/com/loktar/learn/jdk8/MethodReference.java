package com.loktar.learn.jdk8;

import java.util.Arrays;
import java.util.List;

public class MethodReference {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alex", "Bob", "Charlie");
        for (String key : names) {
            System.out.println(key);
        }
        //lamdba表达式
        names.forEach(name -> {
            System.out.println(name);
        });
        //静态方法引用
        names.forEach(System.out::println);
        //实例方法引用
        names.forEach(String::toUpperCase);

        List<Integer> lengths1 = names.stream().map(name -> {
            return name.length();
        }).toList();

        //构造方法引用
        List<Integer> lengths2 = names.stream().map(String::length).toList();

    }
}
