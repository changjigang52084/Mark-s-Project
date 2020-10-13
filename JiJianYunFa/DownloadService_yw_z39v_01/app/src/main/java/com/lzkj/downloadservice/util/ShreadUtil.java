package com.lzkj.downloadservice.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lzkj.downloadservice.app.DownloadApp;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @date 创建时间：2015年6月19日 下午9:04:26
 * @version 1.0
 * @parameter
 */
public class ShreadUtil {
	private String shreadName = "download_app";
	private SharedPreferences sharedPreferences = null;
	private static volatile ShreadUtil shreadUtil = null;
	/**节目id key**/
	public static final String PRM_ID_KEY = "prmIdKey";
	/**当前正在下载的prm key**/
	public static final String CURRENT_PRM_KEY = "currentPrmKey";
	
	/**最大流量限制*/
	public static final String MAX_FOLW_KEY = "max_folw_key";
	/**已使用流量*/
	public static final String USE_FOLW_KEY = "use_folw_key";
	/**初始化已使用流量*/
	public static final String INIT_FOLW_KEY = "init_folw_key";
	/**初始化已使用流量的月份*/
	public static final String INIT_FOLW_MONTH = "init_folw_month";
	
	/**当天下载的流量  key**/
	public static final String DOWNLOAD_FLOW_DAY_KEY = "downloadFlowDay";
	/**当天上传的流量  key**/
	public static final String UPLOAD_FLOW_DAY_KEY = "uploadFlowDay";
	
	/**保存位置的key**/
	public static final String SERVER_KEY = "server_key";
	
	private ShreadUtil() {
		sharedPreferences = DownloadApp.getContext().getSharedPreferences(
				shreadName, Context.MODE_WORLD_WRITEABLE);
	}
	public static ShreadUtil newInstance() {
		if (null == shreadUtil) {
			synchronized (ShreadUtil.class) {
				if (null == shreadUtil) {
					shreadUtil = new ShreadUtil();
				}
			}
		}
		return shreadUtil;
	}

	public int getInt(String paramString) {
		return sharedPreferences.getInt(paramString, 0);
	}

	public void putInt(String paramString, int paramInt) {
		sharedPreferences.edit().putInt(paramString, paramInt).commit();
	}
	public long getLong(String paramString) {
		return sharedPreferences.getLong(paramString, 0);
	}
	
	public boolean putLong(String paramString, long value) {
		return sharedPreferences.edit().putLong(paramString, value).commit();
	}

	public void putString(String key, String value) {
		sharedPreferences.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		return sharedPreferences.getString(key, null);
	}

	/**
	 * 根据key移除掉记录
	 * 
	 * @param key
	 *            key
	 * @return true is remove success,other fail
	 */
	public boolean removeKey(String key) {
		if (sharedPreferences.contains(key)) {
			return sharedPreferences.edit().remove(key).commit();
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
