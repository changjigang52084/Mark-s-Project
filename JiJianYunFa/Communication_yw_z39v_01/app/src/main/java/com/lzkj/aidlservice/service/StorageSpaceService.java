package com.lzkj.aidlservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lzkj.aidlservice.api.impl.ReportDeviceStorageSpaceImpl;
import com.lzkj.aidlservice.service.SyncService.TimerHandler;

/**
 * 本地磁盘存储空间状态服务类
 *
 * @author lyhuang
 * @date 2016-1-29 上午10:57:14
 */
public class StorageSpaceService extends Service {

    private static final String TAG = "StorageSpace";

    /**
     * 检测磁盘空间间隔时间
     */
    private static final long CHECK_STORAGE_DELAY_MILLIS = 1 * 60 * 60 * 1000L;
    /**
     * 检测磁盘状态的定时器
     */
    private TimerHandler mTimerHandler = null;

    /**
     * 汇报磁盘信息的类
     **/
    private ReportDeviceStorageSpaceImpl mReportDeviceStorageSpace;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate.");
        mReportDeviceStorageSpace = new ReportDeviceStorageSpaceImpl();
        mTimerHandler = new TimerHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand.");
        mTimerHandler.removeCallbacksAndMessages(null);
        mTimerHandler.post(checkStorageStateRun);
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable checkStorageStateRun = new Runnable() {
        @Override
        public void run() {
            mTimerHandler.removeCallbacks(checkStorageStateRun);
            mReportDeviceStorageSpace.reportDeviceStorageSpace();
            mReportDeviceStorageSpace.reportTFStorageSpace(); // 汇报TF卡存储空间
            mTimerHandler.postDelayed(checkStorageStateRun, CHECK_STORAGE_DELAY_MILLIS);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy.");
        mTimerHandler.removeCallbacks(checkStorageStateRun);
        mTimerHandler = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
