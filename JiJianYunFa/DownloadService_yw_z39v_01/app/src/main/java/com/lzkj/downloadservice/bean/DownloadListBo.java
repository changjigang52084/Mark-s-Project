package com.lzkj.downloadservice.bean;

import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;

import java.util.List;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月12日 下午6:09:20 
 * @version 1.0 
 * @parameter 下载一组http地址的bo
 */
public class DownloadListBo {
	public List<String> downloadUrlList;
	public IDownloadStateCallback progressListener;
	public String downloadFolder;
	public int type;
	public DownloadListBo(List<String> downloadUrlList, IDownloadStateCallback progressListener, String downloadFolder, int type) {
		this.downloadUrlList = downloadUrlList;
		this.progressListener = progressListener;
		this.downloadFolder = downloadFolder;
		this.type = type;
	}
}
