package com.loktar.learn.jdk13;

public class SwitchCase {
    public static void main(String[] args) {
        int day = 3;
        String dayName = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            default -> "Invalid day";
        };

        System.out.println(dayName);  // 输出: Wednesday

        int day2 = 5;
        String dayType = switch (day2) {
            case 1, 2, 3, 4, 5 -> {
                yield "Weekday";  // 使用yield返回一个值
            }
            case 6, 7 -> {
                System.out.println("It's a weekend!");  // 执行语句
                yield "Weekend";
            }
            default -> {
                yield "Invalid day";
            }
        };

        System.out.println(dayType);  // 输出: Weekend

//        String dayName = switch (getDayOfWeek()) {
//            case 1 -> "Monday";
//            case 2 -> "Tuesday";
//            case 3 -> "Wednesday";
//            case 4 -> "Thursday";
//            case 5 -> "Friday";
//            default -> "Invalid day";
//        };
//
//        System.out.println(dayName);


    }
}
