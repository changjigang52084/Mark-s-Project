package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.util.CommutShardUtil;
import com.lzkj.downloadservice.util.ShreadUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年7月21日 下午8:06:16 
 * @version 1.0 
 * @parameter  接收device id
 */
public class ReceiverDeviceId extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			String deviceId = intent.getStringExtra("deviceId");
			ShreadUtil.newInstance().putString(CommutShardUtil.DEVICE_ID_KEY, deviceId);
		}
	}

}
