package com.lzkj.downloadservice.impl;

public interface HttpDownloadStateCallback {
	/**下载成功*/
	void onSuccess(String httpUrl, int totalSize, int downloadType);
	/**下载失败，失败的原因*/
	void onFail(String httpUrl, String errMsg, int downloadType);
	/**下载进度条*/
	void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType);
	/**开始下载*/
	void onStart(int progress, int totalSize, String httpUrl, int downloadType);
	/**
	 * 取消下载
	 * @param httpUrl 被取消下载的http url
	 * @param downloadType 下载类型
	 */
	void onCancel(String httpUrl, int downloadType);
}
