package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年3月8日 上午9:51:20 
 * @version 1.0 
 * @parameter  监听日期变化的广播
 */
public class DateChangeReceiver extends BroadcastReceiver {
	
	private static final LogTag TAG = LogUtils.getLogTag(DateChangeReceiver.class.getSimpleName(), true);
	
	private Handler mhander = new Handler();
	private static final long DELAYED = 60 * 1000;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//<action android:name="android.intent.action.DATE_CHANGED" />
		if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
			LogUtils.d(TAG, "onReceive", "date change");
			mhander.removeCallbacksAndMessages(null);
			mhander.postDelayed(new Runnable() {
				@Override
				public void run() {
					LogUtils.d(TAG, "onReceive", "date change postDelayed playProgramList");
					//关闭当前activity
					if (null != PlayActivity.getActivity()) {
						PlayActivity.getActivity().finish();
					}
				}
			}, DELAYED);
		}
	}

}
