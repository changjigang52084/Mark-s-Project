package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月18日 下午3:58:29 
 * @version 1.0 
 * @parameter  救援广播action
 */
public class RescueBoradcastReceiver extends BroadcastReceiver {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(RescueBoradcastReceiver.class.getSimpleName(), true);

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG_LOG, "onReceive", "onReceive");
		
	}
}
