package com.unccr.zclh.dsdps.manager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.RecoveryDownloadTaskService;
import com.unccr.zclh.dsdps.download.ResposeDownloadService;
import com.unccr.zclh.dsdps.service.heart.HeartService;
import com.unccr.zclh.dsdps.service.time.TimerSleepScreenService;

public class ServiceManager {

    private static final String TAG = "ServiceManager";

    // 启动service
    private boolean startService = true;

    private ServiceManager(){

    }

    public static final ServiceManager getInstance() {
        return ServiceManagerInstance.INSTANCE;
    }

    private static class ServiceManagerInstance {
        private static final ServiceManager INSTANCE = new ServiceManager();
    }

    public void startAllService(){
        startHeartService();
        startTimerSleepScreenService();
        startResponseDownloadService();
        startRecoveryDownloadTaskService();
    }

    public void stopAllService(){
        stopHeartService();
        stopTimerSleepScreenService();
        stopResponseDownloadService();
        stopRecoveryDownloadTaskService();
    }

    /**
     * 启动心跳服务
     */
    private void startHeartService() {
        startOrStopServiceToCls(HeartService.class, startService);
    }

    /**
     * ͣ停止心跳服务
     */
    private void stopHeartService() {
        startOrStopServiceToCls(HeartService.class, !startService);
    }

    /**
     * 启动定时休眠服务
     */
    private void startTimerSleepScreenService() {
        startOrStopServiceToCls(TimerSleepScreenService.class, startService);
    }

    /**
     * 停止定时休眠服务
     */
    private void stopTimerSleepScreenService() {
        startOrStopServiceToCls(TimerSleepScreenService.class, !startService);
    }

    /**
     * 启动下载服务
     */
    private void startResponseDownloadService(){
        startOrStopServiceToCls(ResposeDownloadService.class, startService);
    }

    /**
     * 停止下载服务
     */
    private void stopResponseDownloadService(){
        startOrStopServiceToCls(ResposeDownloadService.class, !startService);
    }

    /**
     * 启动恢复下载服务
     */
    private void startRecoveryDownloadTaskService(){
        startOrStopServiceToCls(RecoveryDownloadTaskService.class,startService);
    }

    /**
     * 停止恢复下载服务
     */
    private void stopRecoveryDownloadTaskService(){
        startOrStopServiceToCls(RecoveryDownloadTaskService.class,!startService);
    }

    /**
     * 根据类名启动或者停止service
     *
     * @param cls     要启动or停止服务的类名
     * @param isStart true启动服务，false停止服务
     */
    private void startOrStopServiceToCls(Class<? extends Service> cls, boolean isStart) {
        Context context = DsdpsApp.getDsdpsApp();
        Intent serviceIntent = new Intent(context, cls);
        serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isStart) {
            context.startService(serviceIntent);
        } else {
            context.stopService(serviceIntent);
        }
    }
}
