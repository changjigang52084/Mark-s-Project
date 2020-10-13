package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class DownLoadAddressResult implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;

	String resCode;
	String resDesc;
	int urlType;
	int resourcePlayType;
	int playTimes;
	String url;

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResDesc() {
		return resDesc;
	}

	public void setResDesc(String resDesc) {
		this.resDesc = resDesc;
	}

	public int getUrlType() {
		return urlType;
	}

	public void setUrlType(int urlType) {
		this.urlType = urlType;
	}

	public int getResourcePlayType() {
		return resourcePlayType;
	}

	public void setResourcePlayType(int resourcePlayType) {
		this.resourcePlayType = resourcePlayType;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
