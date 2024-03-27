package com.loktar.learn.jdk8;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodReference {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alex", "Bob", "Charlie");
        //常规写法
        for (int i = 0; i < names.size(); i++) {
            System.out.println(names.get(i));
        }
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
        }).collect(Collectors.toList());

        //构造方法引用
        List<Integer> lengths2 = names.stream().map(String::length).collect(Collectors.toList());

    }
}
