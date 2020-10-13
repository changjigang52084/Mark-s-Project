package com.lzkj.aidlservice.manager;

import android.content.Intent;

import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;

/**
 *@author kchang changkai@lz-mr.com
 *@Description:解绑终端管理类
 *@time:2016年4月15日 上午11:19:03
 */
public class UnbindDeviceManager {
	protected static final LogTag TAG = LogUtils.getLogTag(UnbindDeviceManager.class.getSimpleName(), true);
	
	private static volatile UnbindDeviceManager unbindDeviceManager;
	
	private UnbindDeviceManager(){}
	
	public static UnbindDeviceManager newInstance() {
		if (null == unbindDeviceManager) {
			synchronized (UnbindDeviceManager.class) {
				if (null == unbindDeviceManager) {
					unbindDeviceManager = new UnbindDeviceManager();
				}
			}
		}
		return unbindDeviceManager;
	}
	
	/**
	 * 解绑终端
	 */
	public void unbindDevice() {
		LogUtils.i(TAG, "unbindDevice", "unbindDevice");
		SharedUtil.newInstance().clearAll();
		ServiceManager.getInstance().stopAllService();
		ConfigSettings.initAdpressCore();
		
		Intent unbindIntent = new Intent(Constant.UNBIND_ACTION);
		CommunicationApp.get().sendBroadcast(unbindIntent);
	}
}