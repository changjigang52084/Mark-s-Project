package com.unccr.zclh.dsdps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午5:33:17
 * @parameter DateTimeUtil 区域
 */
public class DateTimeUtil {

    private static final String TAG = "DateTimeUtil";

    /**
     * 更新当前时间（毫秒）
     *
     * @return long（毫秒）
     */
    public static final long uptimeMillis() {
        return android.os.SystemClock.uptimeMillis();
    }

    /**
     * 根据用户传入的时间格式返回对应的当前时间字符类型
     * @param format
     * 			传入null则默认使用“yyyy-MM-dd” 例如:yyyy-MM-dd
     * @return
     * 		2015-03-30
     */
    public static String getStringTimeToFormat(String format) {
        if (StringUtil.isNullStr(format)) {
            format = "yyyy-MM-dd";
        }
        return new SimpleDateFormat(format, Locale.CHINA).format(new Date());
    }


    /**
     * 根据用户传入的时间格式返回对应的时间字符类型
     * @param format
     * 			例如:yyyy-MM-dd
     * @return
     * 		2015-03-30
     */
    public static String getStringTimeToFormat(String format,long date) {
        return new SimpleDateFormat(format,  Locale.CHINA).format(date);
    }

    /**
     * 获取当前的时分，并且转换成毫秒
     * @param format 时间字符串的格式(默认 HH:mm)
     * @return 时间毫秒
     */
    public static long getCurrentMillisTime(String format) {
        if(StringUtil.isNullStr(format)){
            format = "HH:mm";
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(dateFormat.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取指定时间的时分秒，并且转换成毫秒
     * @param format 时间字符串的格式(默认 HH:mm)
     * @param time 时间字符串
     * @return 时间毫秒
     */
    public static long getMillisTimeFromStringTime(String format, String time) {
        if(StringUtil.isNullStr(format)){
            format = "HH:mm";
        }
        if (StringUtil.isNullStr(time)) {
            return 0;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据传入的字符串转换成long
     * @param date
     * 	"Jun 28, 2015 12:00:00 AM"
     * @return
     * 	 time is long
     */
    public static long getTimeToStringDate(String date) {
        return new Date(date).getTime();
    }

    /**
     *
     * @param date
     * 			yyyy-MM-dd
     * @return
     */
    public static long getTimeToDate(String date) {
        long millis = -1;
        try {
            millis = new SimpleDateFormat("yyyy-MM-dd", ConfigSettings.SYSTEM_LOCALE).parse(date).getTime();
        } catch (ParseException e) {

        }
        return millis;
    }

    /**
     * 获取当前年
     * @return
     */
    public static String getCurrentyyyy() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", ConfigSettings.SYSTEM_LOCALE);
        return dateFormat.format(new Date());
    }

    /**
     * 获取当前月
     * @return
     */
    public static String getCurrentMM() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", ConfigSettings.SYSTEM_LOCALE);
        return dateFormat.format(new Date());
    }

    /**
     * 获取当前日
     * @return
     */
    public static String getCurrentdd() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", ConfigSettings.SYSTEM_LOCALE);
        return dateFormat.format(new Date());
    }

    /**
     * 获取当前小时
     * @return
     */
    public static String getCurrentHH() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH", ConfigSettings.SYSTEM_LOCALE);
        return dateFormat.format(new Date());
    }


    /**
     * 将传入的string类型的日期转换成Date类型
     * @param date
     * 			string日期类型 例如:2016-03-21 19:07
     * @return
     * 		返回Date类型日期,或者null
     */
    public static Date getDateToStrDate(String date) {
        if (StringUtil.isNullStr(date)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTimeToStrDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
