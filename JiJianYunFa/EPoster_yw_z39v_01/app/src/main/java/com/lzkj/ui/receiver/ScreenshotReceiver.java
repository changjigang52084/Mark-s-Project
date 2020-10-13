package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.ScreenshotUtil;

/**
  * @ClassName: ScreenshotReceiver
  * @Description: TODO 接收截屏广播
  * @author longyihuang
  * @date 2016年6月29日 下午2:53:28
  *
  */
public class ScreenshotReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(ScreenshotReceiver.class.getSimpleName(), true);
	/**截图间隔时间*/
	private static final String KEY_INTERVAL_TIME = "intervalTime";
	/**截图数*/
	private static final String KEY_SHOT_NUMBER = "shotNumber";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG, "onReceive", "executeShot");
		if(null != intent){
			int intervalTime = intent.getIntExtra(KEY_INTERVAL_TIME, 0); // 0
			int shotNumber = intent.getIntExtra(KEY_SHOT_NUMBER, 1); // 1
			ScreenshotUtil.newInstance().excSreenshot(shotNumber, intervalTime);
		}
	}

}
