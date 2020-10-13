package com.lzkj.downloadservice.download;

import java.util.HashMap;
import java.util.Map;

import com.lzkj.downloadservice.bean.DownloadInfo;
import com.lzkj.downloadservice.bean.DownloadInfo.STATE;

/**
 * 管理下载信息的类
 * @author changkai
 *
 */
public class DownloadInfoManager {
	private static Map<String,DownloadInfo> downloadInfoMap = null;
	private static DownloadInfoManager downloadManager = null;
	private DownloadInfoManager() {
		downloadInfoMap = new HashMap<String,DownloadInfo>();
	}
	public static DownloadInfoManager get() {
		if (null == downloadManager) {
			init();
		}
		return downloadManager;
	}
	private static synchronized void init() {
		if (null == downloadManager) {
			downloadManager = new DownloadInfoManager();
		}
	}
	/**
	 * 添加下载记录
	 * @param fileName
	 * 			下载的文件名称
	 * @param downloadInfo
	 * 			下载文件的下载信息
	 */
	public synchronized void addDownloadInfo(String fileName,DownloadInfo downloadInfo) {
		if (null == fileName) {
			return;
		}
		downloadInfoMap.put(fileName, downloadInfo);
	}
	/**
	 * 更新下载进度
	 * @param fileName
	 * 			下载的文件名称
	 * @param downloadProgress
	 * 			下载的进度
	 */
	public synchronized void updateDownloadInfoProgress(String fileName,int downloadProgress) {
		if (null == fileName) {
			return;
		}
		if (downloadInfoMap.containsKey(fileName)) {
			downloadInfoMap.get(fileName).setDownloadProgress(downloadProgress);
			downloadInfoMap.get(fileName).setPercentage(sumPer(downloadInfoMap.get(fileName).getDownloadFileSize(), downloadProgress));
		}
	}
	/**
	 * 计算百分比
	 * @param fileSize
	 * @param downloadProgress
	 * @return
	 */
	private  int sumPer(int fileSize,float downloadProgress) {
		return (int)((downloadProgress/fileSize)*100);
	}
	/**
	 * 更新下载速度
	 * @param fileName
	 * 			文件名
	 * @param downloadSpeed
	 * 			下载速度 单位kb/s
	 */
	public synchronized void updateDownloadInfoSpeed(String fileName,int downloadSpeed) {
		if (null == fileName) {
			return;
		}
		if (downloadInfoMap.containsKey(fileName)) {
			downloadInfoMap.get(fileName).setSpeed(downloadSpeed);
		}
	}
	/**
	 * 更新下载的状态
	 * @param fileName
	 * 		文件名
	 * @param state
	 * 		下载的状态{@link STATE}
	 */
	public synchronized void updateDownloadInfoState(String fileName,STATE state) {
		if (null == fileName) {
			return;
		}
		if (downloadInfoMap.containsKey(fileName)) {
			downloadInfoMap.get(fileName).setDownloadState(state);
		}
	}
	/**
	 * 删除下载的记录信息
	 * @param fileName
	 * 			文件名 
	 */
	public synchronized void deleteDownloadInfo(String fileName) {
		if (null == fileName) {
			return;
		}
		if (downloadInfoMap.containsKey(fileName)) {
			downloadInfoMap.remove(fileName);
		}
	}
	/**
	 * 获取当前所有的下载信息
	 * @return
	 */
	public Map<String,DownloadInfo> getAllDownloadInfo() {
		return downloadInfoMap;
	}
	/**
	 * 根据文件名获取下载的信息
	 * @param fileName
	 * @return
	 */
	public synchronized DownloadInfo getDownloadInfoToFileName(String fileName) {
		if (null == fileName) {
			return null;
		}
		if (downloadInfoMap.containsKey(fileName)) {
			return downloadInfoMap.get(fileName);
		}
		return null;
	}
	
}
