package com.lzkj.downloadservice.receiver;

import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.util.ThreadPoolManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月27日 下午4:38:59 
 * @version 1.0 
 * @parameter  停止所有的下载
 */
public class StopAllDownloadReceiver extends BroadcastReceiver {
	private static final String STOP_DOWNLOAD_ACTION = "com.lzkj.download.STOP_DOWNLOAD_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				DownloadManager.newInstance().cancelAllTask();
			}
		});
	}

}
