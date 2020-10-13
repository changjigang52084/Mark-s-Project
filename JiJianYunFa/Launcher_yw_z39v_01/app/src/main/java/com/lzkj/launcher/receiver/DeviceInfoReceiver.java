package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.util.ShareUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月18日 下午3:33:28 
 * @version 1.0 
 * @parameter  获取设备信息
 */
public class DeviceInfoReceiver extends BroadcastReceiver {

	private static final String TAG = "DeviceInfo";

	private static final String UPDATE_DID_ACTION = "com.lzkj.action.DEVICE_ID_ACTION";
	/**deviceId key**/
	private final static String DEVICEID_KEY = "deviceId";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"DeviceInfoReceiver onReceive action: " + intent.getAction());
		try {
			if (null != intent) {
				if (UPDATE_DID_ACTION.equals(intent.getAction())) {
					String did = intent.getStringExtra(DEVICEID_KEY);
					Log.d(TAG,"DeviceInfoReceiver onReceive did: " + did);
					if (null != did) {
						ShareUtil.newInstance().setString(ShareUtil.DEVICE_ID_KEY, did);
					}
				} else {
					String deviceInfo = intent.getStringExtra("deviceInfo");
					Log.d(TAG,"DeviceInfoReceiver onReceive deviceInfo: " + deviceInfo);
					ShareUtil.newInstance().setString(ShareUtil.DEVICE_ID_KEY, deviceInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}