package com.lzkj.launcher.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lzkj.launcher.app.LauncherApp;

public class ShareUtil {
	public SharedPreferences sharedPreferences;
	private String sharedName = "launcher";
	/**did key**/
	public static final String DEVICE_ID_KEY = "deviceId";
	/**是否绑定的key**/
	public static final String AUTHORIZE_KEY = "authorize";
	/**保存位置的key**/
	public static final String LOCATION_KEY = "loctaion_key";
	/**个推id的key**/
	public static final String GETUI_CLIENT_ID_KEY = "getui_client_id";
	/**激光推id的key**/
	public static final String JPUSH_CLIENT_ID_KEY = "jpush_client_id";
	
	/**版本的key**/
	public static final String VER_CODE_KEY = "verisonCode";
	/**版本信息的key**/
	public static final String VER_INFO_KEY = "verisonInfo";
	
	public static final String ON_HOUR_KEY = "onHour";
	public static final String ON_MINUTE_KEY = "onMinute";
	
	public static final String OFF_HOUR_KEY = "offHour";
	public static final String OFF_MINUTE_KEY = "offMinute";
	
	public static final String ON_OR_OFF_TYPE = "type";
	
	private volatile static ShareUtil shreadUtil = null;
	
	private ShareUtil() {
		sharedPreferences = LauncherApp.getApplication().getSharedPreferences(
				sharedName, Context.MODE_PRIVATE);
	}

	public static ShareUtil newInstance() {
		if (null == shreadUtil) {
			synchronized (ShareUtil.class) {
				if (null == shreadUtil) {
					shreadUtil = new ShareUtil();
				}	
			}
		}
		return shreadUtil;
	}

	/**
	 * 添加String类型的数据到shared里面保存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void setString(String key, String value) {
		if (!isEmpty(key, value)) {
			sharedPreferences.edit().putString(key, value).commit();
		}
	}

	/**
	 * 获取string类型的数据
	 * 
	 * @param key
	 *            键
	 * @return 返回值，如果shared里面没有这个值则返回null
	 */
	public String getString(String key) {
		if (null == key) {
			return null;
		}
		return sharedPreferences.getString(key, null);
	}

	/**
	 * 设置boolean值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void setBoolean(String key, boolean value) {
		if (null == key) {
			return;
		}
		sharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	public boolean setInt(String key, int value) {
		if (null == key) {
			return false;
		}
		return	sharedPreferences.edit().putInt(key, value).commit();
	}
	
	public int getInt(String key) {
		if (null == key) {
			return -1;
		}
		return	sharedPreferences.getInt(key, -1);
	}

	/**
	 * 获取boolean值
	 * 
	 * @param key
	 *            键
	 * @return 默认返回false
	 */
	public boolean getBoolean(String key) {
		if (null == key) {
			return false;
		}
		return sharedPreferences.getBoolean(key, false);
	}

	/**
	 * 判断是否为空，如果是空则返回true
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean isEmpty(String key, String value) {
		if (null == key || null == value) {
			return true;
		}
		return false;
	}

	/**
	 * 清除当前保存在shared的数据
	 */
	public void clearAll() {
		sharedPreferences.edit().clear().commit();
		shreadUtil = null;
		sharedPreferences = null;
	}
}
