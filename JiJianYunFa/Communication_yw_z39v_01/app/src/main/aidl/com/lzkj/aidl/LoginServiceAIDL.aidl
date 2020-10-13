package com.lzkj.aidl;
/**login request result aidl*/
interface LoginServiceAIDL{
	void onSuccess(String resultMsg);
	void onFail(String errMsg);
}