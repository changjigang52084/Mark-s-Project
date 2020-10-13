package com.lzkj.aidlservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.lzkj.aidlservice.adapter.board.gw.GWPowerOnOffManager;
import com.lzkj.aidlservice.adapter.board.ly.LYPowerOnOffManager;
import com.lzkj.aidlservice.adapter.board.smdt.SMDTFeedDogManager;
import com.lzkj.aidlservice.util.Cmd;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月22日 下午7:55:17 
 * @version 1.0 
 * @parameter 看门狗service
 */
public class FeedDogService extends Service {
	private static final LogTag TAG = LogUtils.getLogTag(FeedDogService.class, true);
	/***喂狗的间隔时间**/
	private static final long FEED_DOG_TIME = 35 * 1000;
	
	private FeedDogHandler mHandler = new FeedDogHandler();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Cmd.openWatchDog(GWPowerOnOffManager.WATCH_DOG_PATH,GWPowerOnOffManager.COMMAND);
		mHandler.postDelayed(feedDogRun, FEED_DOG_TIME);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.d(TAG, "onStartCommand", "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	private static class FeedDogHandler extends Handler {}
	
	private Runnable feedDogRun = new Runnable() {
		@Override
		public void run() {
			LogUtils.w(TAG, "feedDogRun", "----------------------");
			Cmd.openWatchDog(GWPowerOnOffManager.WATCH_DOG_PATH,GWPowerOnOffManager.COMMAND);
			mHandler.postDelayed(feedDogRun, FEED_DOG_TIME);
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		disableWatchDog();
		mHandler.removeCallbacks(feedDogRun);
		mHandler.removeCallbacksAndMessages(null);
	}
	
	private void disableWatchDog() {
		LYPowerOnOffManager.get().disableWatchDog();
		SMDTFeedDogManager.getInstance().disableWatchDog();
	}
}
