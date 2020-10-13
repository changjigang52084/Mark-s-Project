package com.lzkj.downloadservice.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.service.ResposeDownloadService;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.ThreadPoolManager;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年7月11日 下午4:06:14 
 * @version 1.0 
 * @parameter  接收取消下载任务的receive
 */
public class CancelDownloadReceiver extends BroadcastReceiver{
	public static final String CANCEL_DOWNLOAD_SIZE_KEY = "cancelDownloadSize";
	private static final LogTag TAG = LogUtils.getLogTag(CancelDownloadReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (null != intent) {
			final Bundle bundle = intent.getExtras();
			ThreadPoolManager.get().addRunnable(new Runnable() {
				@Override
				public void run() {
					if (null != bundle) {
						List<String> downloadList = bundle.getStringArrayList(Constant.CANCEL_DOWNLOAD_FILE_LIST);
						LogUtils.d(TAG, "onReceive", "downloadList size : " + downloadList.size());
						if (null == downloadList || downloadList.isEmpty()) {
							return;
						}
						List<String> flieNames = new ArrayList<String>();
						for (String fileName : downloadList) {
							if (!TextUtils.isEmpty(fileName)) {
								if (fileName.indexOf("http://") == 0) {
									fileName = FileUtil.getFileName(fileName);
								}
								flieNames.add(fileName);
							}
						}
						DownloadManager.newInstance().cancelTaskToFileNames(flieNames);
						LogUtils.d(TAG, "onReceive", "flieNames size : " + flieNames.size());
//						cancelDownloadSize(context, cancelSize);
					} else {
						LogUtils.d(TAG, "onReceive", "bundle is null." );
					}
				}
			});
		}
	}
	
	/**
	 * 取消下载的数
	 * @param context
	 * 			上下文
	 * @param cancelSize
	 * 			取消的下载数
	 */
	private void cancelDownloadSize (Context context, int cancelSize) {
		Intent resposeDownloadIntent = new Intent(context, ResposeDownloadService.class);
		resposeDownloadIntent.putExtra(Constant.DOWNLOAD_TYPE_KEY, Constant.CANCEL_DOWNLOAD);
		resposeDownloadIntent.putExtra(CANCEL_DOWNLOAD_SIZE_KEY, cancelSize);
		resposeDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(resposeDownloadIntent);
	}
}
