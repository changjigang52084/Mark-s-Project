package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.Constant;
/**
 * 守护播放节目apk的广播
 * @author changkai
 *
 */
public class GuardReceiver extends BroadcastReceiver {
	private Handler mHandler = new Handler();
	
	private static final long SLEEP_TIME = 5000;
	
	private Runnable mTimerRun = new Runnable() {
		@Override
		public void run() {
			startEposterApp(CommunicationApp.get());
		}
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent) {
			return;
		}
		
		Toast.makeText(context, R.string.app_error_tip_msg, Toast.LENGTH_LONG).show();
		//休眠5秒钟后,重新启动播放apk
		mHandler.postDelayed(mTimerRun, SLEEP_TIME);
	}
	
	private void startEposterApp(Context cxt) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ComponentName componentName = new ComponentName(Constant.UI_PKG, Constant.UI_ACT);
		intent.setComponent(componentName);
		
		cxt.startActivity(intent);
	}

}
