package com.lzkj.downloadservice.receiver;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.service.ResposeDownloadService;
import com.lzkj.downloadservice.upload.UploadManager;
import com.lzkj.downloadservice.util.HttpUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
/**
 * 退出当前应用
 * @author changkai
 *
 */
public class ExitReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(ExitReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG, "onReceive", "exit download app");
		DownloadManager.newInstance().cancelAllTask();
		UploadManager.newInstance().cancelUploadThreads();
		if (!isDownloadServiceStart(context)) {
			LogUtils.d(TAG, "onReceive", "falg");
			context.stopService(new Intent(context,ResposeDownloadService.class));
		}
		HttpUtil.newInstance().shutdown();
		System.exit(0);
	}
	
	private boolean isDownloadServiceStart(Context cxt) {
		ActivityManager myManager = (ActivityManager) DownloadApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString().equals("com.lzkj.downloadservice.service.ResposeDownloadService")) {
				LogUtils.d(TAG, "isCoreServiceStart", "service"+runningService.get(i).service.getClassName().toString());
				return true;
			}
		}
		return false;
	}
}
