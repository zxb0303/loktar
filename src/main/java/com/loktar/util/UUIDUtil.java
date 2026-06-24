package com.loktar.util;


import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
public class UUIDUtil {
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        log.info("{}", randomUUID());
        log.info("{}", UUID.randomUUID());
    }
}
