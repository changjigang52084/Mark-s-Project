package com.lzkj.aidlservice.adapter.board.ly;

import android.content.Intent;
import android.util.Log;

import com.lzkj.aidlservice.app.CommunicationApp;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年8月17日 下午7:05:35
 * @parameter 凌壹开关机和命令
 */
public class LYPowerOnOffManager {

    private static final String TAG = "PowerOnOffManager";

    private static volatile LYPowerOnOffManager lyPowerOnOffManager;
    /**
     * 喂狗的action
     */
    private static final String WATHC_DOG_ACTION = "android.intent.action.pubds_watchdogenable";
    /**
     * 取消喂狗的action
     */
    private static final String DISABLE_WATHC_DOG_ACTION = "android.intent.action.pubds_watchdogdisable";

    /**
     * 休眠的action
     */
    private static final String SLEEP_ACTION = "android.intent.action.pubds_sleep";

    public boolean isSeelp = false;

    public static LYPowerOnOffManager get() {
        if (null == lyPowerOnOffManager) {
            synchronized (LYPowerOnOffManager.class) {
                if (null == lyPowerOnOffManager) {
                    lyPowerOnOffManager = new LYPowerOnOffManager();
                }
            }
        }
        return lyPowerOnOffManager;
    }

    /**
     * 打开A20看门狗 （打开看门狗和喂狗的方法一样）
     */
    public final void enableWatchDog() {
        isSeelp = false;
        Log.d(TAG, "PowerOnOffManager enableWatchDog enbale Yyt A20 watchdog.");
        Intent intent = new Intent(WATHC_DOG_ACTION);
        CommunicationApp.get().sendBroadcast(intent);
    }

    /**
     * A20喂狗
     */
    public final void feedDog() {
        Log.d(TAG, "PowerOnOffManager feedDog feed Yyt A20 watchdog.");
        Intent intent = new Intent(WATHC_DOG_ACTION);
        CommunicationApp.get().sendBroadcast(intent);
    }

    /**
     * 关闭A20看门狗
     */
    public final void disableWatchDog() {
        Log.d(TAG, "PowerOnOffManager disbleWatchDog disbale Yyt A20 watchdog.");
        Intent intent = new Intent(DISABLE_WATHC_DOG_ACTION);
        CommunicationApp.get().sendBroadcast(intent);
    }

    /**
     * 休眠
     */
    public void sleep() {
        isSeelp = true;
        final Intent sleepIntent = new Intent(SLEEP_ACTION);
        CommunicationApp.get().sendBroadcast(sleepIntent);
        CommunicationApp.get().mAppHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CommunicationApp.get().sendBroadcast(sleepIntent);
                Log.d(TAG, "PowerOnOffManager sleep postDelayed: " + SLEEP_ACTION);
            }
        }, 3000);
        Log.d(TAG, "PowerOnOffManager sleep SLEEP_ACTION: " + SLEEP_ACTION);
    }
}
