package com.lzkj.ui.receiver;

import com.lzkj.ui.util.SharedUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月18日 下午3:33:28 
 * @version 1.0 
 * @parameter  获取设备信息
 */
public class DeviceInfoReceiver extends BroadcastReceiver {
	private static final String UPDATE_DID_ACTION = "com.lzkj.action.DEVICE_ID_ACTION";
	/**deviceId key**/
	private final static String DEVICEID_KEY = "deviceId";
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (null != intent) {
				if (UPDATE_DID_ACTION.equals(intent.getAction())) {
					String did = intent.getStringExtra(DEVICEID_KEY);
					if (null != did) {
						SharedUtil.newInstance().setString(SharedUtil.DEVICE_ID_KEY, did);
					}
				} else {
					String deviceInfo = intent.getStringExtra("deviceInfo");
					SharedUtil.newInstance().setString(SharedUtil.DEVICE_ID_KEY, deviceInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
