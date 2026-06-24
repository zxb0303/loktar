package com.loktar.learn.jdk14;


import lombok.extern.slf4j.Slf4j;
import java.text.NumberFormat;

@Slf4j
public class NumeralFormatting {
    public static void main(String[] args) {
        double value = 12345678;
        NumberFormat nf = NumberFormat.getCompactNumberInstance();
        String formattedValue = nf.format(value);
        log.info("{}", formattedValue);

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
        log.info("{}", formattedValue3);


    }
}
