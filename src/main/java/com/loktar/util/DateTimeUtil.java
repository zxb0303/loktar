package com.loktar.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {
    public static DateTimeFormatter FORMATTER_DATESECOND = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter FORMATTER_DATEMINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter FORMATTER_YEAR = DateTimeFormatter.ofPattern("yyyy");
    public static DateTimeFormatter FORMATTER_FILENAME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static DateTimeFormatter FORMATTER_QYWX_RECEIVE = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    public static DateTimeFormatter FORMATTER_RSS_ITEM_PUB = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);


    public static String getDatetimeStr(LocalDateTime datetime, DateTimeFormatter format) {
        return datetime.format(format);
    }

    public static String getDatetimeStr(LocalDate date, DateTimeFormatter format) {
        return date.format(format);
    }

    public static LocalDateTime parse(String dateStr, DateTimeFormatter format) {
        return LocalDateTime.parse(dateStr, format);
    }

    public static LocalDate parseLocalDate(String dateStr, DateTimeFormatter format) {
        return LocalDate.parse(dateStr, format);
    }

    public static LocalDateTime convertSecondsToDateTime(long seconds) {
        Instant instant = Instant.ofEpochSecond(seconds);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
