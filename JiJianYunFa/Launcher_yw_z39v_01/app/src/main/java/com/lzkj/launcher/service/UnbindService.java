package com.lzkj.launcher.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lzkj.aidl.UnbindAIDL;
import com.lzkj.launcher.MainActivity;
import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.ShareUtil;

/**
 * 解绑服务
 * @author changkai
 *
 */
public class UnbindService extends Service {

	private static final String TAG = "UnbindService";

	private Handler mHandler = new Handler();
	/**解绑当前终端*/
	private UnbindAIDL.Stub unbind = new UnbindAIDL.Stub() {
		@Override
		public void unbind() throws RemoteException {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					Log.d(TAG, "UnbindService unbind open login view.");
					//切换到登录界面
					ShareUtil.newInstance().setBoolean(ShareUtil.AUTHORIZE_KEY, false);
					if(null != MainActivity.getActivity() && 
							!MainActivity.getActivity().isFinishing() && 
							!MainActivity.getActivity().isDestroyed()){
						MainActivity.getActivity().switchLayout(Constant.LOGIN_FRAGMENT);
					}
				}
			});
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		return unbind;
	}
	@Override
	public void onCreate() {
		Log.d(TAG, "UnbindService unbind ui app.");
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		Log.d(TAG, "UnbindService unbind ui app.");
		unbind = null;
		super.onDestroy();
	}
}
