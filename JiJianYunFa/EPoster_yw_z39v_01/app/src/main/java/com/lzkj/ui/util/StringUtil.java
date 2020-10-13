package com.lzkj.ui.util;

import com.lzkj.ui.app.EPosterApp;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月19日 上午11:38:56
 * @parameter
 */
public class StringUtil {
    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断是字符串
     * @return true表示是空, false表示不为空
     */
    public static boolean isNullStr(String str) {
        if (null == str || "".equals(str.trim()) || "null".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * Return a localized string from the application's package's
     * default string table.
     *
     * @param resId Resource id for the string
     */
    public final static String getString(int resId) {
        return EPosterApp.getApplication().getResources().getString(resId);
    }

}
