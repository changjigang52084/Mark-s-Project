package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
/**
 * 退出当前apk停止心跳和连接(未使用)
 * @author changkai
 *
 */
public class ExitReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(ExitReceiver.class.getSimpleName(), true);
	
	@Override
	public void onReceive(Context cxt, Intent arg1) {
		LogUtils.d(TAG, "onReceive", "exit communication app");
	}

}
