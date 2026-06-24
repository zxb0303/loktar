package com.loktar.learn.jdk8;


import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Stream {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Bob", "Alice", "Charlie", "Dave", "Eve");
        //过滤长度大于3的元素
        names.stream().filter(name->{
            return name.length()>3;
        }).forEach(name->{
            log.info("{}", name);
        });
        names.stream().filter(name->name.length()>3).forEach(System.out::println);

        //转换为大写字母并排序
        List<String> uppercaseNames = names.stream().map(name->{
            return name.toUpperCase();
        }).sorted().toList();
        List<String> uppercaseNames2 = names.stream().map(String::toUpperCase).sorted().toList();
        uppercaseNames.forEach(System.out::println);

        //求长度之和
        int totleLength = names.stream().mapToInt(String::length).sum();

    }
}
