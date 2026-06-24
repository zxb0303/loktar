package com.loktar.learn.jdk14;


import lombok.extern.slf4j.Slf4j;
@Slf4j
public class RecordTest {

    public static void main(String[] args) {
        Person person = new Person("alex",18);
        log.info("{}", person.age());
        log.info("{}", person.name());
        log.info("{}", person);
    }

}
