package com.loktar.learn.jdk11;

import java.util.ArrayList;
import java.util.List;

public class CollectionToArray {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Python");
        list.add("C++");
        //jdk8
        String[] array = list.toArray(new String[0]);
        String[] array2 = (String[])list.toArray();
        //jdk11
        String[] array3 = list.toArray(String[]::new);

    }
}
