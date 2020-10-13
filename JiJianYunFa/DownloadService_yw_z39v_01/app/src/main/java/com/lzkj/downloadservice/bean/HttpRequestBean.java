package com.lzkj.downloadservice.bean;

import com.lzkj.downloadservice.interfaces.IRequestCallback;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月4日 下午8:52:01 
 * @version 1.0 
 * @parameter  发送http请求的bean
 */
public class HttpRequestBean {
	private String requestUrl;
	private String requestParm;
	private IRequestCallback requestCallback;
	private String requestTag;
	private int requestRestry = 5;
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getRequestParm() {
		return requestParm;
	}
	public void setRequestParm(String requestParm) {
		this.requestParm = requestParm;
	}
	public IRequestCallback getRequestCallback() {
		return requestCallback;
	}
	public void setRequestCallback(IRequestCallback requestCallback) {
		this.requestCallback = requestCallback;
	}
	public String getRequestTag() {
		return requestTag;
	}
	public void setRequestTag(String requestTag) {
		this.requestTag = requestTag;
	}
	public int getRequestRestry() {
		return requestRestry;
	}
	public void setRequestRestry(int requestRestry) {
		this.requestRestry = requestRestry;
	}
	
}
