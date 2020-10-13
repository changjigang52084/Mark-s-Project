package com.lzkj.launcher.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.lzkj.launcher.receiver.AppInstallReceiver;
import com.lzkj.launcher.receiver.LocationReceiver;
import com.lzkj.launcher.receiver.PushClientReceiver;
import com.lzkj.launcher.receiver.TimeChangeReceive;
import com.lzkj.launcher.service.UploadAppInfoRunable;
import com.lzkj.launcher.util.AppErrorHandler;
import com.lzkj.launcher.util.ConfigSettings;
import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.FileStore;
import com.lzkj.launcher.util.InstallAppTool;

public class LauncherApp extends Application {

    private static final String TAG = "LauncherApp";

    private static LauncherApp launcherApp;
    private AppInstallReceiver appInstallReceiver;
    public LauncherHandler mLauncherHandler = new LauncherHandler();

    private TimeChangeReceive changeReceive;

    private LocationReceiver locationReceiver;
    private PushClientReceiver pushClientReceiver;

    public static LauncherApp getApplication() {
        return launcherApp;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"LauncherApp onCreate.");
        launcherApp = this;
        super.onCreate();
        init();
        initAppErrorHandler();
        registerReceiver();
        registerTimeChangeReceive();
        InstallAppTool.checkAppIsInstall(getApplicationContext());
        mLauncherHandler.post(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                ConfigSettings.MAC_ADDRESS = FileStore.getMac();
                Log.d(TAG, "LauncherApp mac read time: " + (System.currentTimeMillis() - currentTime));
            }
        });
    }

    public static class LauncherHandler extends Handler {
    }

    /**
     * 初始化自定义异常处理的类
     */
    private void initAppErrorHandler() {
        //自定义处理非线程的异常
        AppErrorHandler appErrorHandler = new AppErrorHandler();
        appErrorHandler.init(this);
        Thread.setDefaultUncaughtExceptionHandler(appErrorHandler);
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

    /**
     * 注册安装App的广播
     */
    private void registerReceiver() {
        IntentFilter installFilter = new IntentFilter();
        installFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        installFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        registerReceiver(appInstallReceiver, installFilter);

        IntentFilter pushFilter = new IntentFilter();
        pushFilter.addAction(Constant.LAUNCHER_UPDATE_PUSH_CLIENT_ACTION);
        registerReceiver(pushClientReceiver, pushFilter);

        IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction(Constant.LAUNCHER_UPDATE_LOCATION_ACTION);
        registerReceiver(locationReceiver, locationFilter);
    }

    /**
     * 初始化
     */
    private void init() {
        uploadAppInfo();
        appInstallReceiver = new AppInstallReceiver();
        pushClientReceiver = new PushClientReceiver();
        locationReceiver = new LocationReceiver();
//		openWifi();
    }

    private void uploadAppInfo() {
        Log.d(TAG, "LauncherApp uploadAppInfo.");
        mLauncherHandler.postDelayed(new UploadAppInfoRunable(), 2 * 60 * 1000);
    }

    /**
     * 打开wifi
     */
    private void openWifi() {
        Log.d(TAG, "LauncherApp openWifi.");
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }
}
