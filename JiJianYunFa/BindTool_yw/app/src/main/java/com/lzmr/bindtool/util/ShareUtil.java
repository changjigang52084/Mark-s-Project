package com.lzmr.bindtool.util;


import java.util.Set;

import com.lzmr.bindtool.app.BindToolApp;

import android.content.Context;
import android.content.SharedPreferences;


public class ShareUtil {
	private SharedPreferences sharedPreferences;
	private String sharedName = "bind";
	/**User的key**/
	public static final String USER_KEY = "user_key";
	/**通讯public key**/
	public static final String PUBLIC_KEY = "public_key";
	/**session**/
	public static final String SESSION = "session";
	/**User的account**/
	public static final String USER_ACCOUNT = "user_account";
	/**User的password**/
	public static final String USER_PASSWORD = "user_password";
	/**是否记录账号**/
	public static final String REMEMBER_ACCOUNT = "remember_account";
	/**是否测试模式**/
	public static final String LOGIN_MODE = "login_mode";
	/**是否绑定到极简发布**/
	public static final String BIND_MODE = "bind_mode";
	/**自定义的端口和ip**/
	public static final String IP_POST_KEY = "ip_post";
	
	/**关联绑定**/
	public static final String RELATION_BIND = "relation_bind";
	
	
	private static ShareUtil shreadUtil = null;
	
	private synchronized static void init() {
		if (null == shreadUtil) {
			shreadUtil = new ShareUtil();
		}
	}

	private ShareUtil() {
		sharedPreferences = BindToolApp.getApplication().getSharedPreferences(
				sharedName, Context.MODE_PRIVATE);
	}


	public static ShareUtil newInstance() {
		if (null == shreadUtil) {
			init();
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
	 * 添加Int类型的数据到shared里面保存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void setInt(String key, int value) {
		if (null != key) {
			sharedPreferences.edit().putInt(key, value).commit();
		}
	}
	
	public void setStringSet(String key, Set<String> value) {
		if (null != key && null != value) {
			sharedPreferences.edit().putStringSet(key, value).commit();
		}
	}
	
	public Set<String> getStringSet(String key) {
		if (null != key) {
			return sharedPreferences.getStringSet(key, null);
		}
		return null;
	}
	
	/**
	 * 获取Int类型的数据
	 * 
	 * @param key
	 *            键
	 * @return 返回值，如果shared里面没有这个值则返回-1
	 */
	public int getInt(String key) {
		if (null == key) {
			return -1;
		}
		return sharedPreferences.getInt(key, - 1);
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

	/**
	 * 获取boolean值
	 * 
	 * @param key
	 *            键
	 * @return 默认返回false
	 */
	public boolean getBoolean(String key) {
		if (null == key) {
			return ConfigSettings.IS_SOCIAL;
		}
		return sharedPreferences.getBoolean(key, ConfigSettings.IS_SOCIAL);
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
	}
	
	/**
	 * 清除指定数据
	 */
	public void removeKey(String key) {
		sharedPreferences.edit().remove(key).commit();
	}
}
