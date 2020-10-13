package com.lzkj.ui.app;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.StrictMode;

import com.baidu.apistore.sdk.ApiStoreSDK;
import com.bumptech.glide.Glide;
import com.lzkj.ui.receiver.TimeChangeReceive;
import com.lzkj.ui.service.LocationService;
import com.lzkj.ui.util.AppErrorHandler;
import com.lzkj.ui.util.AppUtil;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.ThreadPoolManager;
/**
 * EPoster Application
 * @author changkai
 */
public class EPosterApp extends Application{
	private static final LogTag TAG = LogUtils.getLogTag(EPosterApp.class.getSimpleName(), true);
	private static final String APP_INFO_ACTION = "com.lzkj.ui.APP_INFO_ACTION";
	/**图片加载次数**/
	private int picLoadIndex = 0;
	/**图片加载次数最大值10 ，到了就清内存**/
	private int maxPicLoadIndex = 10;
	
	private static EPosterApp posterApp;
	/**定位服务对象*/
	private LocationService locationService;
	
	public AppHandler mHandler = new AppHandler();
	
	public static EPosterApp getApplication() {
		return posterApp;
	}

	private TimeChangeReceive changeReceive;

	@Override
	public void onCreate() {
		posterApp = this;
		super.onCreate();
		initSDK();
		initAppErrorHandler();
		initGilde();
		registerTimeChangeReceive();
	}

	/**
	 * 动态组成时间改变received
	 */
	private void registerTimeChangeReceive() {
		if (null == changeReceive) {
			IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
			changeReceive = new TimeChangeReceive();
			registerReceiver(changeReceive, filter);
		}
	}

	private void initGilde() {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				Glide.get(EPosterApp.this).clearDiskCache();
			}
		});
	}

	
	/**
	 * 初始化天气SDK
	 */
	private void initSDK(){
		ConfigSettings.IS_WEIGHT = AppUtil.isInstallApp(EPosterApp.getApplication(), Constants.MALLPOSTER_PKG);
		locationService = new LocationService(getApplicationContext());//初始化定位sdk
		ApiStoreSDK.init(this, Constants.WEATHER_API_KEY);//初始化天气sdk  WEATHER_API_KEY = fd946debc0a78b70c88afebf38aadf60
	}
	
	/**
	 * 初始化自定义异常处理的类
	 */
	private void initAppErrorHandler() {
		// ConfigSettings.isOpenLog = true
		if (ConfigSettings.isOpenLog()) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			.detectLeakedSqlLiteObjects()
			.penaltyLog().penaltyDeath().build());
		}
		//自定义处理非线程的异常
		AppErrorHandler appErrorHandler = new AppErrorHandler();
		appErrorHandler.init(this);
		Thread.setDefaultUncaughtExceptionHandler(appErrorHandler);
	}
	
	/**
	 * 获取定位服务
	 * @param 
	 * @return
	 */
	public LocationService getLocationService(){
		return locationService;
	}
	
	/**
	 * 清除缓存
	 */
	public void clearGlideMemory() {
		picLoadIndex++;
		if (picLoadIndex >= maxPicLoadIndex) {
			LogUtils.d(TAG, "clearGlideMemory", "picLoadIndex : " + picLoadIndex);
			picLoadIndex = 0;
			Glide.get(this).clearMemory();//清除内存缓存,防止OOM
		}
	}
	
	@Override
	public void onLowMemory() {
		Glide.get(this).clearMemory();
		LogUtils.d(TAG, "onLowMemory", "onLowMemory");
		super.onLowMemory();
	}
	
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		switch (level) {
		case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
			break;
		case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
			break;
		case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
			break;
		case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
			break;
		default:
			break;
		}
	}
	public static class AppHandler extends Handler {
	}
}
