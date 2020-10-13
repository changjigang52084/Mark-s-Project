package com.unccr.zclh.dsdps.util;

import com.unccr.zclh.dsdps.app.DsdpsApp;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午5:28:10
 * @parameter StringUtil
 */
public class StringUtil {

        /**
         * 判断字符串是否为空
         *
         * @param str 需要判断的字符串
         * @return true表示为空，false表示不为空
         */
        public static boolean isNullStr(String str) {
            if (str == null || "".equals(str.trim()) || "null".equals(str)) {
                return true;
            }
            return false;
        }

        /**
         * 从应用程序包的默认字符串表返回本地化字符串。
         *
         * @param resId
         * @return
         */
        public final static String getString(int resId){
            return DsdpsApp.getDsdpsApp().getResources().getString(resId);
        }
}























