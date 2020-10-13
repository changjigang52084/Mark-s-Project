package com.lzkj.aidl;
/**login request aidl**/
interface LoginClientAIDL {
	void onLogin(int type,String deviceName,String userName,String password,String macAddress);
	void onCommunication(int type,String macAddress);
	void onCancel();
}