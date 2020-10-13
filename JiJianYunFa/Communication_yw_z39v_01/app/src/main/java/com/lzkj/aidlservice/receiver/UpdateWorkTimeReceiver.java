package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.WorkTimeUtil;

/**
 * 更新工作时间的广播接收器
 *
 * @author lyhuang
 * @version 1.0 
 * @date 2016-1-11 下午4:49:06
 */
public class UpdateWorkTimeReceiver extends BroadcastReceiver {

	private static final LogTag TAG = LogUtils.getLogTag(UpdateWorkTimeReceiver.class.getSimpleName(), true);

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i(TAG, "onReceive", "UpdateWorkTimeReceiver");
		WorkTimeUtil.get().setWorkTime();
	}
}
