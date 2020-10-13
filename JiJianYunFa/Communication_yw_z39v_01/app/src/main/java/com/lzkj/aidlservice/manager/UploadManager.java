package com.lzkj.aidlservice.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.lzkj.aidl.UploadAIDL;
import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.Constant;

/**
 * 上传管理
 * @author changkai
 *
 */
public class UploadManager {
	private static final int SLEEP_TIME = 3000;
	private Handler mHandler = new Handler();
	
	private UploadAIDL mUploadAIDL;
	
	private static volatile UploadManager INSTANCE;
	
	private UploadManager() {
		bindUploadService();
	}
	
	public static UploadManager newInstance() {
		if (null == INSTANCE) {
			synchronized (UploadManager.class) {
				if (null == INSTANCE) {
					INSTANCE = new UploadManager();
				}
			}
		}
		return INSTANCE;
	}
	
	/***绑定上传服务的connect*/
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mUploadAIDL = null;	
		}
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder obj) {
			mUploadAIDL = UploadAIDL.Stub.asInterface(obj);
		}
	};
	
	/**
	 * 绑定上传的服务
	 */
	private void bindUploadService() {
		ComponentName uploadComponentName = new ComponentName(Constant.DOWNLOADSERVICE_PKG, Constant.UPLOADSERVICE_CLS);
		Intent uploadIntent = new Intent();
		uploadIntent.setComponent(uploadComponentName);
		CommunicationApp.get().bindService(uploadIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * 解绑上传service
	 */
	public void unBindUploadService() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (null != mUploadAIDL && mUploadAIDL.asBinder().pingBinder()) {
					CommunicationApp.get().unbindService(serviceConnection);
				}
			}
		}, SLEEP_TIME);
	}
	/**
	 * 接受上传命令
	 * @param command
	 * 			上传命令 
	 */
	public void upload(final int command) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					if (null != mUploadAIDL && mUploadAIDL.asBinder().pingBinder()) {
						mUploadAIDL.uploadCommand(command);
					} else {
						Toast.makeText(CommunicationApp.get(), R.string.upload_fail, Toast.LENGTH_SHORT).show();
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, SLEEP_TIME);
	}
}
