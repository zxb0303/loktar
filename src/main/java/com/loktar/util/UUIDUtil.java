package com.loktar.util;

import java.util.UUID;

public class UUIDUtil {
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(randomUUID());
        System.out.println(UUID.randomUUID().toString());
    }
}
