package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.aidlservice.adapter.board.ly.LYPowerOnOffManager;
import com.lzkj.aidlservice.api.heart.HeartService;
import com.lzkj.aidlservice.app.CommunicationApp;
//import com.lzkj.aidlservice.service.SowingAdvertisementService;
import com.lzkj.aidlservice.service.SyncService;
import com.lzkj.aidlservice.service.TimerSleepScreenService;
import com.lzkj.aidlservice.service.WeatherService;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.Util;

import static com.lzkj.aidlservice.util.Constant.BICHIDANBING_UI_PCK;
import static com.lzkj.aidlservice.util.Constant.ELEVATOR_UI_PCK;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年3月10日 下午4:47:04
 * @parameter 监听时间改变, 并且守护心跳进程
 */
public class TimeChangeReceive extends BroadcastReceiver {

    private static final LogTag TAG = LogUtils.getLogTag(TimeChangeReceive.class.getSimpleName(), true);
    /**
     * 心跳进程的名称
     */
    private static final String HEART_SERVICE_NAME = "com.lzkj.aidlservice.api.heart.HeartService";
//	private static final String HEART_SERVICE_NAME = "com.lzkj.aidlservice:heartservice";
    /**
     * 上传进程的名称
     */
    private static final String UPLOAD_SERVICE_NAME = "com.lzkj.downloadservice:upload";
    /**
     * 同步服务进程的名称
     */
    private static final String SYNC_SERVICE_NAME = "com.lzkj.aidlservice.service.SyncService";

    /**
     * 获取天气预报服务的名称
     */
    private static final String WEATHER_SERVICE_NAME = "com.lzkj.aidlservice.service.WeatherService";
    /**
     * 定时休眠关屏服务的名称
     */
    private static final String TIMER_SLEEP_SCREEN_SERVICE_NAME = "com.lzkj.aidlservice.service.TimerSleepScreenService";
    /**
     * 获取插播广告节目服务的名称
     */
//    private static final String SOWING_ADVERTISEMENT_SERVICE_NAME = "com.lzkj.aidlservice.service.SowingAdvertisementService";

    /**
     * 推送服务进程的名称
     */
    private static final String PUSH_SERVICE_NAME = "com.igexin.sdk.PushService";
//	private static final String SYNC_SERVICE_NAME = "com.lzkj.aidlservice:syncservice";

    @Override
    public void onReceive(final Context context, Intent intent) {
        checkIsDateChange(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /** annotation by cjg 20190614**/
                /*boolean getuiIsRun = AppUtil.isServiceWork(context, PUSH_SERVICE_NAME);
                if (!getuiIsRun) {
                    LogUtils.d(TAG, "onReceive", "getuiIsRun false");
                    PushUitl.initGeTuiPush();
                } else {
                    LogUtils.d(TAG, "onReceive", "getuiIsRun true");
                }*/
                /** annotation by cjg 20190614**/
                if (ConfigSettings.isClientValid()) {
                    boolean heartIsRun = AppUtil.isServiceWork(context, HEART_SERVICE_NAME);
                    LogUtils.d(TAG, "heartIsRun:", heartIsRun);
//					boolean heartIsRun = AppUtil.isAppRunning(HEART_SERVICE_NAME, AppUtil.SERVICE_TYPE);
                    if (!heartIsRun) {//心跳未运行 则马上启动心跳
                        startHeartService(CommunicationApp.get());
                    }
                    boolean uploadLogServiceIsRun = AppUtil.isAppRunning(UPLOAD_SERVICE_NAME, AppUtil.SERVICE_TYPE);
                    LogUtils.d(TAG, "uploadLogServiceIsRun:", uploadLogServiceIsRun);
                    if (!uploadLogServiceIsRun) {//定时上传日志未运行 则马上启动上传日志service
                        startTimerUploadLogService(CommunicationApp.get());
                    }
                    boolean syncServiceIsRun = AppUtil.isServiceWork(context, SYNC_SERVICE_NAME);
                    LogUtils.d(TAG, "syncServiceIsRun:", syncServiceIsRun);
                    if (!syncServiceIsRun) {//定时同步service
                        startSyncService(CommunicationApp.get());
                    }
                    boolean weatherServiceIsRun = AppUtil.isServiceWork(context, WEATHER_SERVICE_NAME);
                    LogUtils.d(TAG, "weatherServiceIsRun:", weatherServiceIsRun);
                    if (!weatherServiceIsRun) {
                        startWeatherService(CommunicationApp.get());
                    }
                    boolean timerSleepScreenServiceIsRun = AppUtil.isServiceWork(context, TIMER_SLEEP_SCREEN_SERVICE_NAME);
                    LogUtils.d(TAG, "timerSleepScreenServiceIsRun:", timerSleepScreenServiceIsRun);
                    if (!timerSleepScreenServiceIsRun) {
                        startTimerSleepScreenService(CommunicationApp.get());
                    }
//                    boolean sowingAdvertisementServiceIsRun = AppUtil.isServiceWork(context, SOWING_ADVERTISEMENT_SERVICE_NAME);
//                    LogUtils.d(TAG, "sowingAdvertisementServiceIsRun:", sowingAdvertisementServiceIsRun);
//                    if (!sowingAdvertisementServiceIsRun) {
//                        startSowingAdvertisementService(CommunicationApp.get());
//                    }
                    int currentApp = ConfigSettings.CURRENT_APP;
                    if (currentApp == HttpConstants.SOCIAL_APP && !LYPowerOnOffManager.get().isSeelp) {
                        boolean mallposterIsRun = AppUtil.isRunningForeground(context, Constant.MALL_POSTER_UI_PKG);
                        boolean mallposterIsInstall = AppUtil.isInstallApp(context, Constant.MALL_POSTER_UI_PKG);
//						if(!mallposterIsRun){
                        if (mallposterIsInstall && !mallposterIsRun) {
                            LogUtils.d(TAG, "onReceive", "startMallPoster");
                            startMallPoster(CommunicationApp.get());
                        }
                    }

                    // 这一段代码是 EERP和极简的逻辑关系
                    if (currentApp == HttpConstants.DSPLUG_APP && !LYPowerOnOffManager.get().isSeelp) {
                        if (isRunForegroundElevator(context)) {
                            LogUtils.d(TAG, "onReceive", "Foreground elevator");
//                            return;
                        }
                        // 救援页面是否正在运行
                        boolean rescueIsRun = AppUtil.isTopActivity(AppUtil.getTopTask(), "com.wconnet.eerp", "com.wconnet.eerp.activity.RescueActivity");
                        LogUtils.d(TAG, "cjg", "rescueIsRun: " + rescueIsRun);
                        // 设置页面是否正在运行
                        // Eposter是否正在前台运行
                        boolean eposterIsRun = AppUtil.isRunningForeground(context, Constant.UI_PKG);
                        LogUtils.d(TAG, "cjg", "eposterIsRun: " + eposterIsRun);
                        // Eposter是否有安装
                        boolean eposterIsInstall = AppUtil.isInstallApp(context, Constant.UI_PKG);
                        LogUtils.d(TAG, "cjg", "eposterIsInstall: " + eposterIsInstall);
                        LogUtils.d(TAG, "cjg", "checkIsSleep: " + TimerSleepScreenService.checkIsSleep());
                        // 如果救援、产线、老化app都没有在运行并且Eposter播放器已经安装了，就打开Eposter播放器
                        /**
                         * 增加了设备是否处于休眠状态的判断
                         * 修改日期：2019年1月19日
                         * 修改者：cjg
                         */
                        if (!eposterIsRun) {
                            if (eposterIsInstall && !eposterIsRun && !rescueIsRun && !TimerSleepScreenService.checkIsSleep()) {
                                startEposter(CommunicationApp.get());
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private boolean isRunForegroundBichiDanBing(Context context) {
        boolean bichiDanBingIsRun = AppUtil.isRunningForeground(context, BICHIDANBING_UI_PCK);
        return bichiDanBingIsRun;
    }

    private boolean isRunForegroundElevator(Context context) {
        boolean elevatorIsRun = AppUtil.isRunningForeground(context, ELEVATOR_UI_PCK);
        return elevatorIsRun;
    }


    /**
     * 判断是否到了日期变更的时间,如果是则发送日期变更的广播
     */
    private void checkIsDateChange(Context context) {
        String hour = Util.getStringTimeToFormat("HHmm");
        if (hour.contains("0000")) {
            AppUtil.notifyProgramPlayList();
            //表示为日期发送改变了
//            if (SharedUtil.newInstance().getString(SharedUtil.WROK_TIMER) != null) {
//                Intent dateChangeIntent = new Intent(Intent.ACTION_DATE_CHANGED);
//                context.sendBroadcast(dateChangeIntent);
//            } else {
//                LogUtils.d(TAG, "checkIsDateChange", "time is 00 : 00");
//				Util.reboot();
//            }
        }
    }

    /**
     * 启动心跳服务
     */
    public void startHeartService(Context context) {
        Intent intent = new Intent(context, HeartService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    /**
     * 启动同步服务
     */
    private void startSyncService(Context context) {
        Intent syncIntent = new Intent(context, SyncService.class);
        syncIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(syncIntent);
    }

    /**
     * 启动天气服务
     */
    private void startWeatherService(Context context) {
        Intent weatherIntent = new Intent(context, WeatherService.class);
        weatherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(weatherIntent);
    }

    /**
     * 启动定时休眠服务
     */
    private void startTimerSleepScreenService(Context context) {
        Intent timerSleepScreenIntent = new Intent(context, TimerSleepScreenService.class);
        timerSleepScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(timerSleepScreenIntent);
    }

//    /**
//     * 启动插播广告服务
//     */
//    private void startSowingAdvertisementService(Context context) {
//        Intent sowingAdvertisementIntent = new Intent(context, SowingAdvertisementService.class);
//        sowingAdvertisementIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(sowingAdvertisementIntent);
//    }

    /**
     * 启动定时上传日志的service
     */
    private void startTimerUploadLogService(Context context) {
        Intent timerLogIntent = new Intent();
        timerLogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName component = new ComponentName(Constant.DOWNLOAD_PKG, Constant.UPLOAD_LOG_SERVICE_CLS);
        timerLogIntent.setComponent(component);
        timerLogIntent.setAction(Constant.UPLOAD_LOG_SERVICE_ACTION);
        context.startService(timerLogIntent);
    }

    private void startMallPoster(Context context) {
        try {
            ComponentName component = new ComponentName(Constant.MALL_POSTER_UI_PKG, Constant.MALL_POSTER_UI_ACT);
            Intent intent = new Intent();
            intent.setComponent(component);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startEposter(Context context) {
        LogUtils.d(TAG, "startEposter", "start-up eposter！！！");
        ComponentName component = new ComponentName(Constant.UI_PKG, Constant.UI_ACT);
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
