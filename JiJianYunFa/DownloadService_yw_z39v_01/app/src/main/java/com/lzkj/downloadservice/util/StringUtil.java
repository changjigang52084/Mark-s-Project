package com.lzkj.downloadservice.util;

import com.lzkj.downloadservice.app.DownloadApp;


/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月19日 上午11:38:56 
 * @version 1.0 
 * @parameter  
 */
public class StringUtil {
	/**
	 * 判断字符串是否为空
	 * @param str
	 * 			需要判断是字符串
	 * @return
	 * 		true表示是空,false表示不为空
	 */
	public static boolean isNullStr(String str) {
        return null == str || "".equals(str.trim()) || "null".equals(str);
    }
	
	/**
     * Return a localized string from the application's package's
     * default string table.
     *
     * @param resId Resource id for the string
     */
    public final static String getString(int resId) {
        return DownloadApp.getContext().getResources().getString(resId);
    }
    /**
     * 去掉后缀获取文件名
     * @param fileName
     * 			完整的文件名
     * @return
     */
    public static String getNameRemoveSuffix(String fileName) {
    	 return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
