package com.lzkj.downloadservice.qiniu.download;
/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月8日 下午3:12:50 
 * @version 1.0 
 * @parameter  
 */
public class DownloadTokenBean {
	private String downloadTokenUrl;
	private int type;
	public String getDownloadTokenUrl() {
		return downloadTokenUrl;
	}
	public void setDownloadTokenUrl(String downloadTokenUrl) {
		this.downloadTokenUrl = downloadTokenUrl;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
