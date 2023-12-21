package com.hdwa.control.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * @description: Date工具类
 * @since: 2023/8/17
 * @version: V4.0
 */
public class DateUtils {

    public static final String sdfDay = "yyyyMMdd";
    public static final String sdfMonth = "yyyyMM";
    public static final String sdfTime = "yyyyMMddHHmmss";
    public static final String sdfHour = "yyyyMMddHH";
    public static final String sdfMinute = "yyyyMMddHHmm";
    // 时间格式-显示
    public final static String date_format_show = "yyyy-MM-dd HH:mm:ss";
    public final static String date_format_show_minute = "yyyy-MM-dd HH:mm";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    /**
     * 东八区时区偏移量
     */
    private final static int ASIA_SHANGHAI = 8;

    /**
     * LocalDate类型转为Date
     *
     * @param localDate LocalDate object
     * @return Date object
     */
    public static Date localDate2Date(LocalDate localDate) {

        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * LocalDateTime类型转为Date
     *
     * @param localDateTime LocalDateTime object
     * @return Date object
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    // 获取指定日期的毫秒
    public static Long getMilliByLocalDateTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // 获取指定日期的秒
    public static Long getSecondsByLocalDateTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    public static String format(LocalDateTime time, String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime time, DateTimeFormatter pattern) {
        return time.format(pattern);
    }

    public static String format(LocalDateTime time) {
        return time.format(FORMATTER);
    }

    public static String formatDate(Date date) {
        return format(date2LocalDateTime(date));
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static LocalDateTime parse(String dateStr, String pattern) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parse(String dateStr, DateTimeFormatter pattern) {
        return LocalDateTime.parse(dateStr, pattern);
    }

    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr, FORMATTER);
    }

    public static Date parseDate(String dateStr) {
        return localDateTime2Date(LocalDateTime.parse(dateStr, FORMATTER));
    }

    // 获取当前时间的指定格式
    public static String formatNow(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    public static LocalDateTime minus(LocalDateTime time, long number, TemporalUnit field) {
        return time.minus(number, field);
    }

    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        return field.between(startTime, endTime);
    }

    public static String getNowTimeStr() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public static String getTimeStr(LocalDateTime date) {
        return date.format(FORMATTER);
    }

    public static String getMinusHour(String dateTime, long hour) {
        LocalDateTime parse = parse(dateTime).minusHours(hour);
        return format(parse);
    }

    public static String getPlusHour(String dateTime, long hour) {
        LocalDateTime parse = parse(dateTime).plusHours(hour);
        return format(parse);
    }

    public static String getPlusDay(String dateTime, long days) {
        LocalDateTime parse = parse(dateTime).plusDays(days);
        return format(parse);
    }

    public static String getPlusHourNow(long hour) {
        LocalDateTime parse = LocalDateTime.now().plusHours(hour);
        return format(parse);
    }

    /**
     * 获取年
     *
     * @return 年
     */
    public static int getYear(LocalDateTime ldt) {
        return ldt.getYear();
    }

    /**
     * 获取月份
     */
    public static int getMonth(LocalDateTime ldt) {
        return ldt.getMonthValue();
    }

    /**
     * 获取月份差 不算满月的月份差（比如7月31号到8月1号仍然算一个月份差）
     */
    public static int getBetweenMonth(LocalDateTime startLdt, LocalDateTime endLdt) {
        int minusMonth = (endLdt.getYear() - startLdt.getYear()) * 12 - endLdt.getMonthValue()
                - startLdt.getMonthValue();
        return minusMonth;
    }

    /**
     * 获取月份最大天数
     */
    public static int getDaylengthOfMonth() {
        LocalDateTime localTime = LocalDateTime.now();
        return localTime.toLocalDate().lengthOfMonth();
    }

    /**
     * 获取年份最大天数
     */
    public static int getDaylengthOfYear() {
        LocalDateTime localTime = LocalDateTime.now();
        return localTime.toLocalDate().lengthOfYear();
    }

    /**
     * 日期格式转换
     *
     * @param srcDateStr
     * @param srcDatePattern
     * @param destDatePattern
     * @return
     */
    public static String transferDateFormat(String srcDateStr, String srcDatePattern, String destDatePattern) {
        DateTimeFormatter srcFommater = DateTimeFormatter.ofPattern(srcDatePattern);
        DateTimeFormatter destFommater = DateTimeFormatter.ofPattern(destDatePattern);
        return LocalDateTime.parse(srcDateStr, srcFommater).format(destFommater);
    }

}