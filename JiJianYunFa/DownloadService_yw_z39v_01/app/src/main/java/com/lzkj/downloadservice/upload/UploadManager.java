package com.lzkj.downloadservice.upload;

import java.util.ArrayList;
import java.util.List;

import com.lzkj.downloadservice.aliyun.upload.UploadThread;

/**
 * 管理上传的类
 * @author changkai
 *
 */
public class UploadManager {
	private List<UploadThread> uploadThreadList;
	private UploadManager(){
		uploadThreadList = new ArrayList<UploadThread>();
	}
	private static UploadManager uploadManager;
	public static UploadManager newInstance() {
		if (null == uploadManager) {
			init();
		}
		return uploadManager;
	}
	
	private static synchronized void init() {
		if (null == uploadManager) {
			uploadManager = new UploadManager();
		}
	}
	/**
	 * 添加上传任务
	 * @param uploadThread
	 */
	public void addUploadThread(UploadThread uploadThread) {
		uploadThreadList.add(uploadThread);
	}
	/**
	 *  添加一组上传任务
	 * @param uploadThreads
	 */
	public void addUploadThreads(List<UploadThread> uploadThreads) {
		uploadThreadList.addAll(uploadThreads);
	}
	/**
	 * 取消所有真在上传的任务
	 */
	public void cancelUploadThreads() {
		for (UploadThread uploadThread : uploadThreadList) {
			  uploadThread.cancelTast();
		}
		uploadThreadList.clear();
	}
}
