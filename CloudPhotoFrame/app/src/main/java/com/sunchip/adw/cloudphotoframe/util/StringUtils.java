package com.sunchip.adw.cloudphotoframe.util;

public class StringUtils {

    //判断是否为空，可能已分配空间有值为 "" (空串)，也可能未分配空间没有值为 null
    public static boolean isEmpty(String str) {
        if ("".equals(str) || str == null) {
            return true;
        } else {
            return false;
        }
    }

    //判断是否不为空
    public static boolean isNotEmpty(String str) {
        if (!"".equals(str) && str != null) {
            return true;
        } else {
            return false;
        }
    }


}
