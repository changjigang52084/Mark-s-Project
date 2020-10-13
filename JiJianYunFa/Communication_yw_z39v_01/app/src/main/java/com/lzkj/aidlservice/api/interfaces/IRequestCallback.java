package com.lzkj.aidlservice.api.interfaces;
/**
 * 监听发送请求的接口
 * @author changkai
 *
 */
public interface IRequestCallback {
	void onSuccess(String result, String httpTag, String requestUrl);
	void onFaile(String errMsg, String httpTag, String requestUrl);
}
