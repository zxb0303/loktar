package com.loktar.learn.jdk14;

import java.text.NumberFormat;

public class NumeralFormatting {
    public static void main(String[] args) {
        double value = 12345678;
        NumberFormat nf = NumberFormat.getCompactNumberInstance();
        String formattedValue = nf.format(value);
        System.out.println(formattedValue);

//        NumberFormat nf2 = NumberFormat.getInstance();
//        String localizedPattern = nf2.toLocalizedPattern();
//        String pattern = nf2.toPattern();
//        System.out.println(localizedPattern);
//        System.out.println(pattern);

        double value2 = 1234.56789;
        NumberFormat nf3 = NumberFormat.getInstance();
        nf3.setMaximumFractionDigits(-1);
        nf3.setMinimumFractionDigits(2);
        String formattedValue3 = nf.format(value2);
        System.out.println(formattedValue3);


    }
}
