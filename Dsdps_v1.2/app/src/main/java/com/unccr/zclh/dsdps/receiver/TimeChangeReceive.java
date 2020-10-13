package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.RecoveryDownloadTaskService;
import com.unccr.zclh.dsdps.download.ResposeDownloadService;
import com.unccr.zclh.dsdps.service.heart.HeartService;
import com.unccr.zclh.dsdps.service.time.TimerSleepScreenService;
import com.unccr.zclh.dsdps.util.AppUtil;
import com.unccr.zclh.dsdps.util.DeviceUtil;
import com.unccr.zclh.dsdps.util.Util;

public class TimeChangeReceive extends BroadcastReceiver {

    private static final String TAG = "DSDPSTimeChangeReceive";

    // 心跳进程的名称
    private static final String HEART_SERVICE_NAME = "HeartService";

    // 定时休眠关屏服务的名称
    private static final String TIMER_SLEEP_SCREEN_SERVICE_NAME = "TimerSleepScreenService";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive action: " + intent.getAction());
//        checkIsDateChange(context);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                boolean isHeartRun = AppUtil.isServiceWork(context, HEART_SERVICE_NAME);
//                Log.d(TAG, "isHeartRun: " + isHeartRun);
//                if (!isHeartRun) {
//                    startHeartService(DsdpsApp.getDsdpsApp());
//                }
                boolean timerSleepScreenServiceIsRun = AppUtil.isServiceWork(context, TIMER_SLEEP_SCREEN_SERVICE_NAME);
                Log.d(TAG, "timerSleepScreenServiceIsRun: " + timerSleepScreenServiceIsRun);
                if (!timerSleepScreenServiceIsRun) {
                    startTimerSleepScreenService(DsdpsApp.getDsdpsApp());
                }
            }
        }).start();
    }

    /**
     * 判断是否到了时期变更的时间，如果是则发送日期变更的广播
     *
     * @param context
     */
    private void checkIsDateChange(Context context) {
        String hour = Util.getStringTimeToFormat("HHmm");
        Log.d(TAG, "checkIsDateChange hour: " + hour);
        if (hour.contains("0000")) {
            DeviceUtil.rebootDevice();
        }
    }

    // 启动心跳服务
    private void startHeartService(Context context) {
        Intent intent = new Intent(context, HeartService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    /**
     * 启动定时休眠服务
     */
    private void startTimerSleepScreenService(Context context) {
        Intent timerSleepScreenIntent = new Intent(context, TimerSleepScreenService.class);
        timerSleepScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(timerSleepScreenIntent);
    }

    private void startResponseDownloadService(Context context){
        Intent responseDownloadIntent = new Intent(context, ResposeDownloadService.class);
        responseDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(responseDownloadIntent);
    }

    private void startRecoveryDownloadTaskService(Context context){
        Intent recoveryDownloadTaskIntent = new Intent(context, RecoveryDownloadTaskService.class);
        recoveryDownloadTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(recoveryDownloadTaskIntent);
    }
}
