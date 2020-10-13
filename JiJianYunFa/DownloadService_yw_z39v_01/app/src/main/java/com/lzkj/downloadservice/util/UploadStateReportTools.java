package com.lzkj.downloadservice.util;

import android.content.Intent;

import com.lzkj.downloadservice.app.DownloadApp;

/**
  * @ClassName: UploadStateReportTools
  * @Description: TODO 上传状态汇报工具
  * @author longyihuang
  * @date 2016年6月15日 上午11:47:37
  *
  */
public class UploadStateReportTools {
	/**上传状态广播的action*/
	public static final String UPLOAD_STATE_ACTION = "com.yqkj.download.UPLOAD_STATE_ACTION";
	/***状态**/
	private static final String STATE = "state";
	/***上传文件路径**/
	private static final String UPLOAD_FILE_PATH = "uploadFilePath";

	/**
	 * 发生上传状态的广播
	 * @param downloadState 下载状态
	 * @param httpUrl 下载地址
	 * @param downList 取消下载列表
	 */
	public static void sendUploadFileStateReceive(int uploadState, String uploadFilePath) {
		Intent uploadStateIntent = new Intent(UPLOAD_STATE_ACTION);
		uploadStateIntent.putExtra(STATE, uploadState);
		uploadStateIntent.putExtra(UPLOAD_FILE_PATH, uploadFilePath);
		DownloadApp.getContext().sendBroadcast(uploadStateIntent);
	}
}
