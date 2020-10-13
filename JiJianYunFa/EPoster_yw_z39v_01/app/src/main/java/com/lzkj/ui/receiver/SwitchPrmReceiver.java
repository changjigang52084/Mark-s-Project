package com.lzkj.ui.receiver;

import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年7月23日 下午5:27:19 
 * @version 1.0 
 * @parameter  切换节目
 */
public class SwitchPrmReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(SwitchPrmReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			LogUtils.i(TAG, "onReceive", "SwitchPrmReceiver: update program");
			ProgramPlayManager.getInstance().updateProgramList();
		}
	}
}
