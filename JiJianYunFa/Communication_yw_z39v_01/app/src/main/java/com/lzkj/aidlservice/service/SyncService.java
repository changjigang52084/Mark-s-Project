package com.lzkj.aidlservice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.lzkj.aidlservice.api.interfaces.ISyncCallBack;
import com.lzkj.aidlservice.api.sync.RequestSyncDeviceInfo;
import com.lzkj.aidlservice.api.sync.RequestSyncProgramRunnable;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.SharedUtil;

/**
 * @author kchang changkai@lz-mr.com
 * @Description: 同步设备信息和节目信息
 * @time:2016年1月12日 上午10:24:04
 */
public class SyncService extends Service implements ISyncCallBack {

    private static final String TAG = "SyncService";
    private TimerHandler mTimerHandler = null;
    private long zeroTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerSyncProgramReceiver();
        mTimerHandler = new TimerHandler();
        init();
        Log.d(TAG, "SyncService onCreate.");
    }

    /**
     * 注册同步节目的广播
     */
    private void registerSyncProgramReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.SYNC_DEVICE_PROGRAM);
        registerReceiver(syncProgramReceiver, intentFilter);
    }

    private BroadcastReceiver syncProgramReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "action: " + intent.getAction());
            RequestSyncProgramRunnable requestSyncProgram = new RequestSyncProgramRunnable(SyncService.this);
            mTimerHandler.postDelayed(requestSyncProgram, 0);
        }
    };

    private void init() {
        Log.d(TAG, "SyncService init.");
        syncDeviceInfoToDelayMillis(100);
        syncDevicePrmToDelayMillis(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long deviceTime = ConfigSettings.getSycnDeviceTime() == zeroTime ? HttpConstants.TEN_MINUTE_LONG : ConfigSettings.getSycnDeviceTime();
        Log.d(TAG, "onStartCommand deviceTime: " + deviceTime);
        syncDeviceInfoToDelayMillis(deviceTime);
        long prmTime = ConfigSettings.getSycnPrmTime() == zeroTime ? HttpConstants.TEN_MINUTE_LONG : ConfigSettings.getSycnPrmTime();
        Log.d(TAG, "onStartCommand prmTime: " + prmTime);
        syncDevicePrmToDelayMillis(prmTime);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void syncToTag(String tag) {
        Log.d(TAG, "SyncService syncToTag tag: " + tag);
        if ("RequestSyncUpdateProgram".equals(tag)) {
            long prmTime = getSyncTime(ConfigSettings.getSycnPrmTime());
            Log.d(TAG, "syncToTag prmTime: " + prmTime);
            syncDevicePrmToDelayMillis(prmTime);
        } else if ("RequestSyncDeviceInfo".equals(tag)) {
            long deviceTime = getSyncTime(ConfigSettings.getSycnDeviceTime());
            Log.d(TAG, "syncToTag deviceTime: " + deviceTime);
            syncEposter(deviceTime);
        }
    }

    private long getSyncTime(long syncTime) {
        if (syncTime < HttpConstants.TEN_MINUTE_LONG) {
            // 最低的更新设备时间为十分钟
            return HttpConstants.TEN_MINUTE_LONG;
        }
        return syncTime;
    }

    /**
     * 同步设备节目
     */
    private void syncDevicePrmToDelayMillis(long delayMillis) {
        Log.d(TAG, "SyncService syncDevicePrmToDelayMillis delayMillis: " + delayMillis);
        RequestSyncProgramRunnable requestSyncProgram = new RequestSyncProgramRunnable(this);
        mTimerHandler.postDelayed(requestSyncProgram, delayMillis);
    }

    /**
     * 同步设备信息的请求
     */
    private void syncDeviceInfoToDelayMillis(long delayMillis) {
        Log.d(TAG, "SyncService syncDeviceInfoToDelayMillis delayMillis: " + delayMillis);
        syncEposter(delayMillis);
    }

    private void syncEposter(long delayMillis) {
        Log.d(TAG, "SyncService syncEposter delayMillis: " + delayMillis);
        RequestSyncDeviceInfo requestSyncDeviceInfo = new RequestSyncDeviceInfo(this);
        mTimerHandler.postDelayed(requestSyncDeviceInfo, delayMillis); // 延迟delayMillis分钟执行requestSyncDeviceInfo方法
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeCallbacksAndMessages(null);
        mTimerHandler = null;
        Log.d(TAG, "SyncService onDestroy.");
    }

    /**
     * 更新did广播
     */
    private BroadcastReceiver updateDeviceIdBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String did = intent.getStringExtra(Constant.DEVICEID_KEY);
            Log.d(TAG, "SyncService onReceive update did: " + did);
            SharedUtil.newInstance().setString(SharedUtil.DEVICE_ID, did);
        }
    };

    public static class TimerHandler extends Handler {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
