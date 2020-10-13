package com.lzkj.downloadservice.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/10/18.
 * @Parameter 日期工具类
 */

public class DateUtil {

    private static volatile DateUtil sDateUtil;

    private DateUtil() {}

    public static DateUtil getInstance() {
        if (null == sDateUtil) {
            synchronized (DateUtil.class) {
                if (null == sDateUtil) {
                    sDateUtil = new DateUtil();
                }
            }
        }
        return  sDateUtil;
    }

    /**
     * @return  获取本月的第一天时间为long
     */
    public long getCurrentMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime().getTime();
    }

    /**
     * @param day (-1表示昨天,1表示明天)
     * @return 获取当前月的某一天
     */
    public String getCurrentMonthDate(int day, String dateFormat) {
        if (null == dateFormat) {
            dateFormat = "yyyyMMdd";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * @param dateFormat
     * @return 获取当前日期
     */
    public String getCurrentDate(String dateFormat) {
        if (null == dateFormat) {
            dateFormat = "yyyyMMdd";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        return simpleDateFormat.format(new Date());
    }

}
