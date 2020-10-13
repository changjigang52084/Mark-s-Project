package com.lzkj.launcher.service;


import com.lzkj.launcher.impl.LoginCallback;

/**
 * 管理登录状态的类 
 * @author changkai
 *
 */
public class LoginCallbackManager {
	private static volatile LoginCallbackManager loginCallbackManager = null;
	private LoginCallbackManager (){}
	private LoginCallback loginCallbackList = null;
	public static LoginCallbackManager newInstance() {
		if (null == loginCallbackManager) {
			synchronized (LoginCallbackManager.class) {
				if (null == loginCallbackManager) {
					loginCallbackManager = new LoginCallbackManager();
				}
			}
		}
		return loginCallbackManager;
	}
	/**
	 * 添加成功的消息
	 * @param loginCallback
	 */
	public void addLoginCallback(LoginCallback loginCallback) {
		if (null == loginCallback) {
			return;
		}
		loginCallbackList = loginCallback;
	}
	/***
	 * 设置成功的消息
	 * @param msg
	 */
	public void setSuccess(String msg) {
		if(loginCallbackList != null){
			loginCallbackList.onSuccess(msg);
		}
	}
	/**
	 * 设置失败的消息
	 * @param errMsg
	 */
	public void setFail (String errMsg) {
		if(loginCallbackList != null){
			loginCallbackList.onFail(errMsg);
		}
	}
}