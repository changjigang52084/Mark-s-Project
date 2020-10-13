package com.lzkj.aidl;
interface LoginServiceAIDL{
	void onSuccess(String resultMsg);
	void onFail(String errMsg);
}