package com.lzkj.downloadservice.interfaces;
/**
 * 监听发送请求的接口
 * @author changkai
 *
 */
public interface IRequestCallback {
	void onSuccess(String result, String httpUrl, String requestTag);
	void onFaile(String errMsg, String httpUrl, String requestTag);
}
