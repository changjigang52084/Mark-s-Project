package com.lzkj.downloadservice.util;

import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月16日 上午11:30:49 
 * @version 1.0 
 * @parameter  Communication shared
 */
public class CommutShardUtil {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(CommutShardUtil.class.getSimpleName(), true);
	/**设备id*/
	public static final String DEVICE_ID_KEY = "device_id";
	/**工作服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"*/
	public static final String WORK_SERVER_KEY = "work_server";
	/**回执服务器地址**/
	public static final String HEART_SERVER_KEY = "heart_server";
	/**消息服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"*/
	public static final String MESSAGE_SERVER_ID = "message_server";
	/**注册服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"*/
	public static final String REGISTER_SERVER_ID = "register_server";
	/**adpress服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"*/
//	public static final String ADPRESS_SERVER_ID = "adpress_server";
	/**redis服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"*/
	public static final String REDIS_SERVER_ID = "reids_server";
	/**下载计划回执的adpressDataPackage对象"*/
	public static final String DWONLOADPLAN_ADPRESSDATAPACKAGE = "download_panl_adpressDataPackage";
	/**节目列表的id*/
	public static final String PROGRAM_LIST_ID = "program_id";
	private SharedPreferences sharedPreferences;
	private String sharedName = "pull_parm";
	private static volatile CommutShardUtil shardUtil;
	private  CommutShardUtil(){
	}
	public static CommutShardUtil newInstance() {
		if (null == shardUtil) {
			synchronized (CommutShardUtil.class) {
				if (null == shardUtil) {
					shardUtil = new CommutShardUtil();
				}	
			}
		}
		return shardUtil;
	}
	/**
	 * 根据key获取值
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		initPreferences();
		String str = sharedPreferences.getString(key, null);
		return str;
	} 
	
	private void initPreferences() {
		try {
			sharedPreferences = null;
			Context communiationAppsContext = DownloadApp.
											 getContext().
											 createPackageContext(
													 Constant.COMMUNIACTION_PKG,
													 Context.CONTEXT_IGNORE_SECURITY);
			sharedPreferences = communiationAppsContext.getSharedPreferences(sharedName, Context.MODE_WORLD_READABLE);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public long getLong(String key) {
		if (null == key) {
			return 0;
		}
		initPreferences();
		return sharedPreferences.getLong(key, 0);
	}
}
