package com.hdwa.alarm.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static final String SDF_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String SDF_HOUR = "yyyy-MM-dd HH";
    public static final String SDF_DAY = "yyyy-MM-dd";
    public static final String SDFDAY = "yyyyMMdd";
    public static final String SDF_DOT_DAY = "yyyy.MM.dd";
    public static final String SDFDAY2 = "yyyy.MM.dd";
    public static final String SDFSECOND = "yyyyMMddHHmmss";
    public static final String SDF_MONTH = "yyyy-MM-01";
    public static final String SDF_DOT_MONTH = "yyyy.MM";
    public static final String SDFMONTH2 = "yyyy.MM";
    public static final String SDF_YEAR = "yyyy-01-01";
    public static final String SDF_ONLY_HOUR_MINUTE = "HH:mm";
    public static final String sdfDay = "yyyyMMdd";
    public static final String sdfMonth = "yyyyMM";
    public static final String sdfTime = "yyyyMMddHHmmss";
    public static final String sdfTimeNotDate = "HHmmss";
    public static final String sdfHour = "yyyyMMddHH";
    public static final String sdfMinute = "yyyyMMddHHmm";
    // 时间格式-显示
    public final static String date_format_show = "yyyy-MM-dd HH:mm:ss";
    public final static String date_format_show_minute = "yyyy-MM-dd HH:mm";
    public final static String SDF_SECOND2 = "yyyy.MM.dd HH:mm:ss";
    public final static String SDF_SECOND3 = "yyyy.MM.dd HH:mm";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    /**
     * 东八区时区偏移量
     */
    public final static int ASIA_SHANGHAI = 8;


    /**
     * 获取两个日期的差 field参数为ChronoUnit.* 默认累加的方式 重要说明： 计算月份差 计算的是否满月，年份同理，
     * 区别1：Period只考虑到日期，ChronoUnit.between()计算到具体时分秒
     * 区别2：Period只计算月份差，不考虑年份是否一样，ChronoUnit.between()计算考虑年份
     * :Period只考虑到日期，field.between(startTime, endTime)会计算到具体时间 period.getMonths();
     * field.between 比如：6月6号8点到8月6号7点： 2 1 比如：6月6号8点到8月6号9点： 2 2 比如：6月5号到8月6号： 2 2
     * 比如：6月7号到8月6号： 1 1
     *
     * @param startTime
     * @param endTime
     * @param field     单位(年月日时分秒)
     * @return
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        return field.between(startTime, endTime);
    }

    public static String format(Date date, String format) {
        if (null != date && !StrUtil.isBlank(format)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            if (date instanceof DateTime) {
                TimeZone timeZone = ((DateTime) date).toCalendar().getTimeZone();
                if (null != timeZone) {
                    sdf.setTimeZone(timeZone);
                }
            }

            return format((Date) date, (DateFormat) sdf);
        } else {
            return null;
        }
    }

    public static String format(Date date, DateFormat format) {
        return null != format && null != date ? format.format(date) : null;
    }

    /**
     * 获得当前 [给定日期时间 格式] 的字符串
     *
     * @param _dtFormat 日期格式1  yyyy-MM-dd HH:mm:ss
     *                  日期格式2  yyyy-MM-dd
     * @return 给定日期格式的字符串
     */
    public static String getNowDateTime(String _dtFormat) {
        String currentdatetime = "";
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dtFormat = new SimpleDateFormat(_dtFormat);
            currentdatetime = dtFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentdatetime;
    }

    public static Date getDateFormat(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateString = formatter.format(date);
        ParsePosition pos = new ParsePosition(8);
        return formatter.parse(dateString, pos);
    }

    //获取时间差方法
    public static double getTimeDiffToDay(Date startDate, Date endDate) {
        long diffMS = cn.hutool.core.date.DateUtil.betweenMs(startDate, endDate);
        //(1000 * 60 * 60 * 24)
        Long dayMSRate = 86400000L;
        //(1000 * 60 * 60)
        Long hourMSRate = 3600000L;

        long days = diffMS / dayMSRate;
        //获取时
        long hours = diffMS % dayMSRate / hourMSRate;
        double hour = hours / 2.4;
        BigDecimal b = new BigDecimal(hour * 0.1);
        double hour1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return days + hour1;
    }

    /**
     * 获取间隔秒数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long betweenTwoTimeSecond(String startTime, String endTime) {
        return betweenTwoTime(startTime, endTime, ChronoUnit.SECONDS);
    }

    /**
     * 获取间隔时间
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long betweenTwoTime(String startTime, String endTime, ChronoUnit field) {
        return betweenTwoTime(parse(startTime), parse(endTime), field);
    }

    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr, FORMATTER);
    }

    public static Date parseDate(String dateStr) {
        return localDateTime2Date(LocalDateTime.parse(dateStr, FORMATTER));
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

    public static String format(LocalDateTime time) {
        return time.format(FORMATTER);
    }

    public static String formatDate(Date date) {
        return format(date2LocalDateTime(date));
    }

    /**
     * Date转换为LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static void main(String[] args) {
        System.out.println(getDateFormat(new Date(), ""));
    }
}