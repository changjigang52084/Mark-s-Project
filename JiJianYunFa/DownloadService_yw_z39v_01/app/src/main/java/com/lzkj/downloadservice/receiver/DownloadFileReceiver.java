package com.lzkj.downloadservice.receiver;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.download.HttpDownloadTask;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月29日 上午10:40:44 
 * @version 1.0 
 * @parameter 不控制流量的下载
 */
public class DownloadFileReceiver extends BroadcastReceiver implements IDownloadStateCallback {
	private static final LogTag TAG = LogUtils.getLogTag(DownloadFileReceiver.class.getSimpleName(), true);

	private static final String ACTION = "com.lzkj.downloadservice.DOWNLOAD_FILE_LIST_ACTION";
	private static final String DOWNLOAD_LIST_KEY = "downloadListKey";
	private static final String FOLDER_KEY = "folderKey";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			ArrayList<String> downloadList = intent.getStringArrayListExtra(DOWNLOAD_LIST_KEY);
			String folder = intent.getStringExtra(FOLDER_KEY);
			HttpDownloadTask httpDownloadFile = new HttpDownloadTask();
			httpDownloadFile.addDownloadListTask(downloadList, this, folder , Constant.DOWNLOAD_FILE);
		}
	}

	@Override
	public void onSuccess(String httpUrl, int totalSize, int downloadType) {
		LogUtils.d(TAG, "onSuccess", "httpUrl: " + httpUrl);
	}

	@Override
	public void onFail(String httpUrl, String errMsg, int downloadType) {
		LogUtils.d(TAG, "onSuccess", "httpUrl: " + httpUrl);
	}

	@Override
	public void updateProgreass(int progress, int totalSize, String httpUrl,
			int downloadType) {
	}

	@Override
	public void onStart(int progress, int totalSize, String httpUrl,
			int downloadType) {
	}

	@Override
	public void onCancel(String httpUrl, int downloadType) {
		LogUtils.d(TAG, "onSuccess", "httpUrl: " + httpUrl);
	}
}
