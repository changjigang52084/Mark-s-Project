package com.lzkj.downloadservice.impl;
/**
 * 监听发送请求的接口
 * @author changkai
 *
 */
public interface RequestCallback {
	void onSuccess(String result, String httpUrl);
	void onFaile(String errMsg, String httpUrl);
}
