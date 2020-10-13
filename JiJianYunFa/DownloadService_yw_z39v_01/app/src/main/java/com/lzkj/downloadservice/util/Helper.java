package com.lzkj.downloadservice.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.qiniu.android.utils.StringUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月19日 下午8:32:32 
 * @version 1.0 
 * @parameter  
 */
public class Helper {
	/**
	 * 获取uuid
	 * @return
	 */
   public static String getUUID() {  
        UUID uuid = UUID.randomUUID();  
        String str = uuid.toString();  
        // 去掉"-"符号  
        String temp = str.replaceAll("-", "");  
        return temp;  
    } 

	/**
	 * 根据用户传入的时间格式返回对应的时间字符类型
	 * @param format
	 * 			例如:yyyy-MM-dd
	 * @return
	 * 		2015-03-30
	 */
	public static String getStringTimeToFormat(String format) {
		return new SimpleDateFormat(format,  Locale.CHINA).format(new Date());
	}
	/**
	 * 获取当前时间的前一个小时
	 * @return
	 */
	public static int getCurrentHourBefore() {
		String currentH = new SimpleDateFormat("HH", Locale.CHINA).format(new Date());
		int hour = Integer.parseInt(currentH);
		hour = hour == 0 ? 23 : (hour - 1);
		return hour;
	}
	/**
	 * 本机是否安装了app
	 * @param context
	 * 			上下文
	 * @param appPackageName
	 * 			包名
	 * @return
	 * 		true表示已安装，false未安装
	 */
	public static boolean isInstallApp(Context context, String appPackageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		for (PackageInfo packageInfo : packageInfos) {
			 if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
					String packageName = packageInfo.packageName;
					if (packageName.equals(appPackageName)) {
						return true;
					}
			 }
		}
		return false;
	}
	/**
	 * 重启
	 */
	public static void reboot() {
		 try {
			 String cmd = "su -c reboot";
			 Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据带有参数的的url获取无参数的http url
	 * @param downloadUrlTokens 一组带参数的url
	 * @return 返回null或者是一组无参数的url
	 * @throws MalformedURLException url地址错误
	 */
	public static List<String> getDownloadHttpUrls(List<String> downloadUrlTokens) throws MalformedURLException {
		if (null == downloadUrlTokens || downloadUrlTokens.isEmpty()) {
			return downloadUrlTokens;
		}
		List<String> downloadHttpUrls = new ArrayList<String>();
		for (String httpUrlToken : downloadUrlTokens) {
			if (!TextUtils.isEmpty(httpUrlToken)) {
				URL url = new URL(httpUrlToken);
				
				StringBuffer stringBuffer = new StringBuffer(4);
				stringBuffer.append(url.getProtocol());
				stringBuffer.append("://");
				stringBuffer.append(url.getAuthority());
				stringBuffer.append(url.getPath());
				downloadHttpUrls.add(stringBuffer.toString());
				
				url = null;
				stringBuffer = null;
			}
		}
		
		return downloadHttpUrls;
	}
}
