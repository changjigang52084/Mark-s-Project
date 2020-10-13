package com.lzkj.downloadservice.receiver;

import com.lzkj.downloadservice.util.ConfigSettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年7月10日 上午11:37:49 
 * @version 1.0 
 * @parameter  接收重启redis的receive
 */
public class RetryRedisReceive extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConfigSettings.initAdpressCore();
	}

}
