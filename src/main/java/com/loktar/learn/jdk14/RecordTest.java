package com.loktar.learn.jdk14;

public class RecordTest {

    public static void main(String[] args) {
        Person person = new Person("alex",18);
        System.out.println(person.age());
        System.out.println(person.name());
        System.out.println(person);
    }

}
