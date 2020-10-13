package com.lzkj.downloadservice.impl;

public interface UpdateProgressCallback {
	void update(String httpUrl);
	void updateDownloadSize(int index);
}
