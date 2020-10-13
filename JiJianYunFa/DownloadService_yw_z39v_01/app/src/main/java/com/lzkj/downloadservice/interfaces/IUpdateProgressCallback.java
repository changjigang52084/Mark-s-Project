package com.lzkj.downloadservice.interfaces;

public interface IUpdateProgressCallback {
	void update(String httpUrl);
	void updateDownloadSize(int index);
}
