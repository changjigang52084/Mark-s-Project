package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年3月10日 下午4:47:04 
 * @version 1.0 
 * @parameter  监听时间改变
 */
public class TimeChangeReceive extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(TimeChangeReceive.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		//判断当前的service是否被杀死
		LogUtils.d(TAG, "onReceive", "time change receive..");
	}

}
