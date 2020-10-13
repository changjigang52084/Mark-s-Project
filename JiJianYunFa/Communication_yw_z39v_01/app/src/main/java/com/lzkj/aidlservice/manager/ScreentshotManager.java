 package com.lzkj.aidlservice.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lzkj.aidl.ScreentshotAIDL;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/**
 * 绑定截图服务的aidl客户端
 * @author changkai
 *
 */
public class ScreentshotManager {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(ScreentshotManager.class.getSimpleName(), true);
	private ScreentshotAIDL screentshotAIDL = null;
//	private Handler mHandler = new Handler();

	/**休眠时间3秒*/
	private static final long SLEEP_TIME = 3000;
	public ScreentshotManager() {
//		bindShotService();
	}
	/**
	 * 绑定截图服务
	 */
	private void bindShotService() {
		ComponentName componentName = new ComponentName(Constant.UI_PKG, Constant.SCREENSHOTSERVICE_CLS);
		Intent shotServiceIntent = new Intent();
		shotServiceIntent.setComponent(componentName);
		CommunicationApp.get().bindService(
				shotServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	/**服务连接对象**/
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			screentshotAIDL = null;
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			screentshotAIDL = ScreentshotAIDL.Stub.asInterface(service);
		}
	};
	
	/**
	 * 执行截图命令
	 * @param intervalTime
	 * 			截图间隔时间
	 * @param shotNumber
	 * 			截图数
	 */
	public void executShotCommand(final int intervalTime, final int shotNumber) {
		//发送广播，通知应用截屏
		Intent shotIntent = new Intent();
		shotIntent.setAction(Constant.SCREEN_SHOTS_ACTION);
		shotIntent.putExtra("intervalTime", intervalTime);
		shotIntent.putExtra("shotNumber", shotNumber);
		CommunicationApp.get().sendBroadcast(shotIntent);
	}

}
