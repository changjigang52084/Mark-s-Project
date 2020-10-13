package com.xunixianshi.vrshow.interfaces;

public interface DownLoadInterface {
	boolean download(String downLoadType, int resourceId, String downLoadName, String downLoadIconUrl, String packageName, String fileSize);
	boolean reDownload(String downLoadType, int resourceId, int downloadId);
}
