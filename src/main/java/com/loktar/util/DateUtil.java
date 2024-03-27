package com.loktar.util;


import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {


    public static final String DATEFORMATYEAR = "yyyy";
    public static final String DATEFORMATMONTH = "yyyy-MM";
    public static final String DATEFORMATDAY = "yyyy-MM-dd";

    public static final String DATEFORMATMINUTE = "yyyy-MM-dd HH:mm";

    public static final String DATEFORMATMINUTESTR = "yyyyMMddHHmm";
    public static final String DATEFORMATMINUTESECONDSTR = "yyyyMMddHHmmss";

    public static final String DATEFORMATSECOND = "yyyy-MM-dd HH:mm:ss";

    public static final String DATEFORMATMILLISECOND = "yyyy-MM-dd HH:mm:ss SSS";




    /**
     * 按指定的格式，把Date转换成String 如date为null,返回null
     *
     * @param date
     *            Date参数
     * @param format
     *            日期格式
     * @return String
     */
    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 按指定的格式，把string转换成Date 如string为空或null，返回null
     *
     * @param string
     * @param format
     * @return
     * @throws ParseException
     */

    public static Date parase(String string, String format)  {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按年月日格式，把String转换成Date 如果String为空或者null，返回null
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date string2Date(String dateString)  {
        Date dateTime = null;
        if (!StringUtils.isEmpty(dateString)) { //如果string时间参数不是空
            final SimpleDateFormat df = new SimpleDateFormat(DATEFORMATDAY); //年月日时间格式化
            Date date = null;
            try {
                date = df.parse(dateString); //String转换Date
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTime = new Date(date.getTime());
        }
        return dateTime;
    }

    /**
     * 获取当前系统时间
     *
     * @return
     */
    public static Date getSysDate() {
        Calendar calender = Calendar.getInstance();
        return calender.getTime();
    }

    /**
     * 取一天的开始时间 精确到秒 如果date为null，返回null
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getDayFirstSecond(Date date) {
        if (date == null) {
            return null;
        }
        String str = format(date, DATEFORMATDAY) + " 00:00:00";
        return str;
    }

    /**
     * 取一天的开始时间 精确到秒 如果string为""，返回null
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String getDayFirstSecond(String date) {
        if (date.equals("")) {
            return null;
        }
        String ret = "";
        try {
            ret = getDayFirstSecond(string2Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 取一天的结束时间 精确到秒 如果date为null，返回null
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getDayLastSecond(Date date) {
        if (date == null) {
            return null;
        }
        final String str = format(date, DATEFORMATDAY) + " 23:59:59";
        return str;
    }

    /**
     * 取一天的结束时间 精确到秒 如果string为""，返回null
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String getDayLastSecond(String date) {
        if (date.equals("")) {
            return null;
        }
        String ret = "";
        try {
            ret = getDayLastSecond(string2Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 取一天的开始时间 精确到毫秒
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getDayFirstTime(Date date) throws Exception {
        if (date == null) {
            return null;
        }
        final String str = format(date, DATEFORMATDAY) + " 00:00:00 000";
        return parase(str, DATEFORMATMILLISECOND);
    }

    /**
     * 取一天的结束时间 精确到毫秒
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getDayLastTime(Date date) throws Exception {
        if (date == null) {
            return null;
        }
        final String str = format(date, DATEFORMATDAY) + " 23:59:59 999";
        return parase(str, DATEFORMATMILLISECOND);
    }

    /**
     * 获取昨天的日期
     *
     * @param strDate
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getYestoday(String strDate) throws ParseException {
        if (null != strDate && strDate.length() > 0) {
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(parase(strDate, DATEFORMATDAY));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            final String str = format(cal.getTime(), DATEFORMATDAY);
            return parase(str, DATEFORMATDAY);
        }
        return null;
    }

    /**
     * 获取明天的日期
     *
     * @param
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getTomorrow() throws ParseException {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(getSysDate());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        final String str = format(cal.getTime(), DATEFORMATDAY);
        return parase(str, DATEFORMATDAY);
    }

    /**
     * 获取指定日期下一天的日期
     *
     * @param
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getNextDay(Date someDate)  {
        final Calendar ca = Calendar.getInstance();
        ca.setTime(someDate);
        ca.add(Calendar.DAY_OF_MONTH, 1);
        final String str = format(ca.getTime(), DATEFORMATDAY);
        try {
            return parase(str, DATEFORMATDAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据当前日期返回本月的最后一天
     *
     * @param someDate
     * @return
     */
    public static Date getLastDayOfMonth(Date someDate) {
        final Calendar ca = Calendar.getInstance();
        ca.setTime(someDate); // someDate 为你要获取的那个月的时间
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }

    /**
     * 得到本月最后一天的日期
     */
    public static Date getLastDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }

    /**
     * 当前日期 yyyy-MM-dd
     *
     * @return
     */
    public static String getToday() {
        Calendar ca = Calendar.getInstance();
        String str = format(ca.getTime(), DATEFORMATDAY);
        return str;
    }

    /**
     * 当前日期上个月 yyyy-MM-dd
     *
     * @return
     */
    public static String getLastMonthToday() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) - 1);
        String str = format(ca.getTime(), DATEFORMATDAY);
        return str;
    }

    /**
     * 当前日期上个星期yyyy-MM-dd
     *
     * @return
     */
    public static String getLastWeekToday() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -7);
        String str = format(ca.getTime(), DATEFORMATDAY);
        return str;
    }

    /**
     * 当前日期 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getTodayToSecond() {
        Calendar ca = Calendar.getInstance();
        String str = format(ca.getTime(), DATEFORMATSECOND);
        return str;
    }

    /**
     * 当前日期-月 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getLastMonthTodayToSecond() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) - 1);
        String str = format(ca.getTime(), DATEFORMATSECOND);
        return str;
    }

    /**
     * 当前日期-一周 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getLastWeekTodayToSecond() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -7);
        String str = format(ca.getTime(), DATEFORMATSECOND);
        return str;
    }

    /**
     * 得到本月第一天的日期
     */
    public static Date getStartDayOfMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }

    /**
     * 得到本月第一天的日期
     */
    public static Date getStartDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }

    /**
     * 得到本月最后一天的日期
     */
    public static Date getEndDayOfMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }

    /**
     * 得到本月最后一天的日期
     */
    public static Date getEndDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }

    /**
     * 得到下个月第一天的日期
     */
    public static Date getStartDayOfNextMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.add(Calendar.MONTH, +1);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }

    /**
     * 得到下个月第一天的日期
     */
    public static Date getStartDayOfNextMonth(String dateStr) throws ParseException {
        Date date = parase(dateStr, DATEFORMATMONTH);
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.add(Calendar.MONTH, +1);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }

    /**
     * 获取指定日期所在周的周一
     */
    public static Date getMonday(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_WEEK, 2);// 老外将周日定位第一天，周一取第二天
        return cDay.getTime();
    }

    /**
     * 获取指定日期所在周日
     */
    public static Date getSunday(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        if (Calendar.DAY_OF_WEEK == cDay.getFirstDayOfWeek()) { // 如果刚好是周日，直接返回
            return date;
        } else {// 如果不是周日，加一周计算
            cDay.add(Calendar.DAY_OF_YEAR, 7);
            cDay.set(Calendar.DAY_OF_WEEK, 1);
            return cDay.getTime();
        }
    }

    /**
     * 获取本年的第一天
     */
    public static Date getFirstDayOfYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 获取本年的第一天
     */
    public static Date getFirstDayOfYear(String dateStr) throws ParseException{
        Date date = parase(dateStr, DATEFORMATYEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 获取本年的最后一天
     */
    public static Date getLastDayOfYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        return calendar.getTime();
    }

    /**
     * 获取本年的最后一天
     */
    public static Date getLastDayOfYear(String dateStr) throws ParseException{
        Date date = parase(dateStr, DATEFORMATYEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        return calendar.getTime();
    }

    /**
     * 获取下一年的第一天
     */
    public static Date getFirstDayOfNextYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, +1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 获取下一年的第一天
     */
    public static Date getFirstDayOfNextYear(String dateStr) throws ParseException{
        Date date = parase(dateStr, DATEFORMATYEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, +1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 获取昨天的日期
     *
     * @param
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static String getYestoday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE, -1);
        calendar.set(calendar.HOUR_OF_DAY, 0);
        calendar.set(calendar.MINUTE, 0);
        return format(calendar.getTime(), DATEFORMATMINUTE);
    }

    public static String getYestodayStr() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE, -1);
        calendar.set(calendar.HOUR_OF_DAY, 0);
        calendar.set(calendar.MINUTE, 0);
        return format(calendar.getTime(), DATEFORMATDAY);
    }


    /**
     * 获取当前时间分钟之前 精确到分钟
     */
    public static String getBeforMinutesSysDate(int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -minute);
        return  format(calendar.getTime(), DATEFORMATMINUTE);
    }

    /**
     *  获取当前时间分钟之前 精确到分钟
     */
    public static String getMinuteSysDate(){
        Calendar calendar = Calendar.getInstance();
        return format(calendar.getTime(), DATEFORMATMINUTE);
    }

}
