package com.lzkj.downloadservice.qiniu.impl;

import java.util.List;

import com.lzkj.downloadservice.qiniu.download.DownloadTokenBean;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月8日 下午3:40:12 
 * @version 1.0 
 * @parameter  获取七牛下载路径的集合
 */
public interface QiNiuDownloadTokenCallback {
	/**
	 * 根据token url下载文件
	 * @param list
	 * 			一组下载文件的带token的url
	 * @param type
	 * 			下载文件的类型
	 * @param downloadFolder
	 * 			文件下载路径(文件夹)
	 */
	void downloadTokenList(List<String> list, int type, String downloadFolder);
}
