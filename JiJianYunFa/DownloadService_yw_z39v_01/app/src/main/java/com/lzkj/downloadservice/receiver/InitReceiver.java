package com.lzkj.downloadservice.receiver;

import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.service.ResposeDownloadService;
import com.lzkj.downloadservice.util.ShreadUtil;
import com.lzkj.downloadservice.util.ThreadPoolManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月27日 下午4:13:15 
 * @version 1.0 
 * @parameter  初始化download,停止所有下载和清除sqlite数据，shared数据
 */
public class InitReceiver extends BroadcastReceiver {
	private static final String INIT_DOWNLOAD_ACTION = "com.lzkj.download.INIT_ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				DownloadManager.newInstance().cancelAllTask();
				SQLiteManager.getInstance().delDownloadTaskTable();
				SQLiteManager.getInstance().deleteDownloadTableData();
				ShreadUtil.newInstance().clearAll();
				Context context = DownloadApp.getContext().getApplicationContext();
				Intent stopDownloadService = new Intent(context, ResposeDownloadService.class);
				stopDownloadService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.stopService(stopDownloadService);
			}
		});
	}

}
