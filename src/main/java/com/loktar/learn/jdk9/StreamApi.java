package com.loktar.learn.jdk9;

import java.util.List;
import java.util.stream.Stream;

public class StreamApi {
    public static void main(String[] args) {
        // takeWhile() 方法示例
        List<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6)
                .takeWhile(n -> n < 4)
                .toList();
        System.out.println("takeWhile 示例：" + numbers);

        // dropWhile() 方法示例
        List<Integer> numbers2 = Stream.of(1, 2, 3, 4, 5, 6)
                .dropWhile(n -> n < 4)
                .toList();
        System.out.println("dropWhile 示例：" + numbers2);

        // ofNullable() 方法示例
        String name = null;
        List<String> names = Stream.ofNullable(name)
                .toList();
        System.out.println("ofNullable 示例：" + names);

        // iterate() 方法的重载示例
        List<Integer> evenNumbers = Stream.iterate(0, n -> n < 10, n -> n + 2)
                .toList();
        System.out.println("iterate 重载示例：" + evenNumbers);

        // 可能不会按顺序打印
        Stream.of("Java", "Python", "C++")
                .parallel()
                .forEach(System.out::println);

        // 将按"Java", "Python", "C++"的顺序打印
        Stream.of("Java", "Python", "C++")
                .parallel()
                .forEachOrdered(System.out::println);

        //Stream 接口中的新方法示例
        Stream.of("Java", "Python", "C++")
                .forEachOrdered(System.out::println);
    }

}
