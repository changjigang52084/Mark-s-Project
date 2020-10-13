package com.xunixianshi.vrshow.util;

import com.google.gson.Gson;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 将map转化为json
     * @param map
     * @return
     */
    public static String MapToJson(Map<? extends Object, ? extends Object> map) {
        String string = "";
        if (map != null) {
            Gson gson = new Gson();
            string = gson.toJson(map);
        }
        return string;
    }

    public static boolean isBlank(CharSequence var0) {
        int var1;
        if (var0 != null && (var1 = var0.length()) != 0) {
            for (int var2 = 0; var2 < var1; ++var2) {
                if (!Character.isWhitespace(var0.charAt(var2))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static String subString(String str, String key, boolean isHead) {
        String subString = "";
        if (str != null && str.contains(key)) {
            if (isHead) {
                subString = str.substring(0, str.indexOf(key));
            } else {
                subString = str.substring(str.indexOf(key));
            }
        }
        return subString;
    }

    public static String getTotalSizeString(long totalSize) {

        if (totalSize >= 1024) {
            if (totalSize / 1024 >= 1024) {
                return totalSize / 1024 / 1024 + "MB";
            }
            return totalSize / 1024 + "KB";
        }
        return totalSize + "Byte";
    }

    public static String getNumToString(long num){
        String numString ="";
        if(num >=100*100*100*100){
            numString = num/(100*100*100*100)+"亿";
        }
        else if(num >=100*100){
            numString = num/(100*100)+"万";
        }
        else {
            numString  +=  num;
        }
        return numString;
    }

    /**
     * 自动转换数据大小单位
     *
     * @ClassName StringUtil
     * @author HeChuang
     * @time 2016/11/1 15:49
     */
    public static String getDownloadSpeedString(long speed) {
        if (speed >= 8) {
            if (speed / 8 >= 1024) {
                if (speed / 8 / 1024 >= 1024) {
                    return speed / 8 / 1024 / 1024 + "MB/s";
                }
                return speed / 8 / 1024 + "KByte/s";
            }
            return speed / 8 + "Byte/s";
        }
        return speed + "Bit/s";
    }

    /**
     * 清除所有HTML标签并返回文本字符串
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        return htmlStr.trim().trim(); // 返回文本字符串
    }

    /**
     * 判断字符串是否为空
     *
     * @ClassName StringUtil
     * @author HeChuang
     * @time 2016/11/1 15:49
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNum(String str){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
