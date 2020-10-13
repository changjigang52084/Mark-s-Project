package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.util.StringUtil;
import com.lzmr.client.core.util.LogUtils;
import com.lzmr.client.core.util.LogUtils.LogTag;

/**
 * 接收开关机（亮屏和黑屏）状态的广播
 *
 * @author lyhuang
 * @date 2016-1-28 下午4:04:10
 */
public class ShartupShutdownReceiver extends BroadcastReceiver {
	private static final LogTag LOG_TAG = LogUtils.getLogTag(ShartupShutdownReceiver.class.getSimpleName(), true);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i(LOG_TAG, "onReceive", "ShartupShutdownReceiver.");
		if(null == intent){
			return;
		}
		String deviceState = intent.getStringExtra("deviceState");
		if(StringUtil.isNullStr(deviceState)){
			LogUtils.e(LOG_TAG, "onReceive", "DeviceState is null.");
			return;
		}
		LogUtils.e(LOG_TAG, "onReceive", "DeviceState is deviceState :" + deviceState);
		
//		ReportDeviceStatusManager.get().reportDeviceState(deviceState);
	}
}
