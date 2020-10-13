package com.unccr.zclh.dsdps.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.lang.reflect.Method;

public class PackageUtils {

	/**
	 * 获取版本名
	 *
	 * @param context 上下文
	 *
	 * @return 版本名
	 */
	public static String getVersionName(Context context) {
		//获取包管理器
		PackageManager pm = context.getPackageManager();
		//获取包信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			//返回版本号
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 获取版本号
	 *
	 * @param context 上下文
	 * @return 版本号
	 */
	public static int getVersionCode(Context context) {
		// 获取包管理器
		PackageManager pm = context.getPackageManager();
		// 获取包信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			// 返回版本号
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取固件版本号
	 *
	 * @return
	 */
	public static String getDeviceDisplayId() {
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.build.display.id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serial;
	}
}
