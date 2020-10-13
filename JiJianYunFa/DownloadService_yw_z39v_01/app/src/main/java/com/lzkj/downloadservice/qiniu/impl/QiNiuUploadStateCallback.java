package com.lzkj.downloadservice.qiniu.impl;

public interface QiNiuUploadStateCallback {
	/**下载成功*/
	void onUploadSuccess(String uploadLocleFile);
	/**下载失败，失败的原因*/
	void onUploadFail(String uploadLocleFile, String errMsg);
	/**下载进度条*/
	void onUploadUpdateProgreass(int progress, String uploadLocleFile);
}
