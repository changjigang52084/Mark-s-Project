package com.lzkj.aidlservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lzkj.aidlservice.adapter.board.rxw.RXWPowerOnOffManager;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.WorkTimeUtil;

/**
 * 定时休眠屏幕的service
 */
public class TimerSleepScreenService extends Service {

    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(TimerSleepScreenService.class.getSimpleName(), true);

    private static final long DELAY_MILLIS = 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        CommunicationApp.get().mAppHandler.postDelayed(timerCheckScreenRunnable, DELAY_MILLIS);
    }

    private Runnable timerCheckScreenRunnable = new Runnable() {
        @Override
        public void run() {
            //判断当前是否为休眠时间段，并且没有在播放安抚视频和dsplug
            //如果在播放dsplug就发送关闭dsplug的广播，如果在播放安抚视频就等待下次检验的时间进行判断。
            if (checkIsSleep()) {
                LogUtils.d(TAG, "run", "checkIsSleep is true...");
                // 如果Eposter播放器正在播放节目
                if (checkIsPlayEposter()) {
                    LogUtils.d(TAG, "run", "checkIsPlayEposter is true...");
                    //发送关闭dsplug广播
                    closePlayApp();
                    closeScreen();
                    stopSelf();
                    return;
                }
                // 如果当前界面正在救援界面
                if (checkIsPlayAppeaseVideo()) {
                    LogUtils.d(TAG, "run", "checkIsPlayAppeaseVideo is true...");
                    //等待安抚视频播放完
                    CommunicationApp.get().mAppHandler.postDelayed(this, DELAY_MILLIS);
                    return;
                } else {
                    LogUtils.d(TAG, "run", "checkIsPlayAppeaseVideo is false...");
                    closeScreen();
                    stopSelf();
                }
            } else {
                LogUtils.d(TAG, "run", "checkIsSleep is false...");
                stopSelf();
            }
        }
    };

    /***
     * 判断当前的时间是否为休眠时间段
     *
     * @return 返回true表示为休眠状态
     */
    public static boolean checkIsSleep() {
        return WorkTimeUtil.get().checkCurrentTimeIsSleepTimeSlot();
    }

    /**
     * 判断EERP是否在前台运行
     *
     * @return 返回true表示正在调用EERP拨打救援电话
     */
    private boolean checkIsPlayAppeaseVideo() {
        return AppUtil.isRunningForeground(getApplicationContext(), Constant.ELEVATOR_UI_PCK);
    }

    /**
     * 判断Eposter是否在前台运行
     *
     * @return 返回true表示Eposter正在前台播放节目
     */
    private boolean checkIsPlayEposter() {
        return AppUtil.isRunningForeground(getApplicationContext(), Constant.UI_PKG);
    }

    /**
     * 关闭屏幕
     */
    private void closeScreen() {
        RXWPowerOnOffManager.sleep();// 瑞芯微
//        LYPowerOnOffManager.get().sleep();// 凌壹
    }

    /**
     * 关闭播放软件
     */
    private void closePlayApp() {
        LogUtils.d(TAG, "closePlayApp", "close eposter.");
        AppUtil.stopApp("no poster app need to start");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
