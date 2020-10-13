package com.unccr.zclh.dsdps.service.heart;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.NetworkUtil;
import com.unccr.zclh.dsdps.util.SharedUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月11日 上午10:43:12
 * @parameter HeartService
 */

public class HeartService extends Service {

    private static final String TAG = "HeartService";

    // 心跳默认间隔时间5秒
    private static final int HEART_TIMER_DELAY = 5 * 1000;

    // 上报设备间隔时间1H
    private static final int DEVICE_TIMER_DELAY = 60 * 60 * 1000;

    private Timer mTimer;

    // 心跳定时器任务
    private TimerTask mHeartTimerTask;

    // 上报设备定时器任务
    private TimerTask mDeviceTimerTask;

    private static RequestHelper requestHelper = new RequestHelper();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_STICKY;
    }

    private void init() {
        stopTimer();
        mTimer = new Timer();
        mHeartTimerTask = new HeartTimerTask();
        mDeviceTimerTask = new DeviceTimerTask();
        mTimer.schedule(mHeartTimerTask, 0, HEART_TIMER_DELAY);
        mTimer.schedule(mDeviceTimerTask, 5000, DEVICE_TIMER_DELAY);
    }

    private class HeartTimerTask extends TimerTask {

        @Override
        public void run() {
            Log.d(TAG,"isOpen: " + isOpen());
            if(!isOpen()){
                SharedUtil.newInstance().setInt("state",2);
            }
            if(NetworkUtil.isNetworkConnected(HeartService.this)){
                startHeart();
            }
        }
    }

    private class DeviceTimerTask extends TimerTask {

        @Override
        public void run() {
            startDevice();
        }
    }

    private void startHeart() {
        requestHelper.heartBeatApi();
    }

    private void startDevice() {
        requestHelper.deviceBeatApi();
    }

    // 停止Timer
    private void stopTimer() {
        if (mTimer != null) {
            try {
                mTimer.cancel();
                mTimer = null;
                mHeartTimerTask = null;
                mDeviceTimerTask = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 屏幕是否亮屏
    private boolean isOpen() {
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy.");
        stopTimer();
        System.exit(0);
    }
}
