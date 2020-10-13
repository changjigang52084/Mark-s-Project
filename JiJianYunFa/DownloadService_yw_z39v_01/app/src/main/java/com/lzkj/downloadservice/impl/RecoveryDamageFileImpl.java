package com.lzkj.downloadservice.impl;

import java.util.ArrayList;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.download.HttpDownloadTask;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FlowManager;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月23日 上午10:14:33 
 * @version 1.0 
 * @parameter  恢复已损坏的素材 进行下载
 */
public class RecoveryDamageFileImpl implements IDownloadStateCallback {
	private static final LogTag TAG = LogUtils.getLogTag(RecoveryDamageFileImpl.class.getSimpleName(), true);
	
	/**
	 * 执行批量下载
	 * @param downloadUrlList
	 * 			要下载的素材文件
	 */
	public void extecuDownload(ArrayList<String> downloadUrlList) {
		FileUtil.getInstance().writerDownloadTask("extecuDownload_" + JSON.toJSONString(downloadUrlList), true);
		HttpDownloadTask httpDownloadFile = new HttpDownloadTask();
		httpDownloadFile.addDownloadListTask(downloadUrlList, this, null, Constant.DOWNLOAD_PRMFILE);
		httpDownloadFile.execu();
	}
	
	@Override
	public void onSuccess(String httpUrl, int totalSize, int downloadType) {
		LogUtils.d(TAG, "onSuccess", "httpUrl: " + httpUrl);
		sendSuccessAction(FileUtil.getFileName(httpUrl)); 
	}
	/**
	 * 发生下载素材成功的action
	 * @param fileName
	 * 			文件名
	 */
	private void sendSuccessAction(String fileName) {
		Intent intent = new Intent(Constant.MATERIAL_REPAIR_SUCCESS_ACTION);
		intent.putExtra(Constant.MATERIAL_NAME, fileName);
		DownloadApp.getContext().sendBroadcast(intent);
	}

	@Override
	public void onFail(String httpUrl, String errMsg, int downloadType) {
		LogUtils.d(TAG, "onFail", "httpUrl: " + httpUrl + ",errMsg:" + errMsg);
	}

	@Override
	public void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType) {
		LogUtils.d(TAG, "updateProgreass", "httpUrl: " + httpUrl);
		FlowManager.getInstance().addDownloadFlow(progress);
	}

	@Override
	public void onStart(int progress, int totalSize, String httpUrl, int downloadType) {
		LogUtils.d(TAG, "onStart", "httpUrl: " + httpUrl);
	}

	@Override
	public void onCancel(String httpUrl, int downloadType) {
		
	}
}
