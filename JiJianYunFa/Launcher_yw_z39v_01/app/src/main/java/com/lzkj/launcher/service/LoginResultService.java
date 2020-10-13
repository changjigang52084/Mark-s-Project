package com.lzkj.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lzkj.aidl.LoginServiceAIDL;

/**
 * 获取请求登录以后的结果
 * @author changkai
 *
 */
public class LoginResultService extends Service {

	private static final String TAG = "LoginResult";

	/**
	 * 被绑定的类
	 */
	private LoginServiceAIDL.Stub loginServiceAIDL = new LoginServiceAIDL.Stub() {
		@Override
		public void onSuccess(String resultMsg) throws RemoteException {
			//切换到登录成功的界面
			Log.d(TAG, "LoginResultService onSuccess resultMsg: " + resultMsg);
			if (null != resultMsg) {
				LoginCallbackManager.newInstance().setSuccess(resultMsg);
			}
		}
		@Override
		public void onFail(String errMsg) throws RemoteException {
			//提示登录失败
			Log.d(TAG, "LoginResultService onFail errMsg: " +errMsg);
			LoginCallbackManager.newInstance().setFail(errMsg);
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		return loginServiceAIDL;
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		loginServiceAIDL = null;
		super.onDestroy();
	}
}
