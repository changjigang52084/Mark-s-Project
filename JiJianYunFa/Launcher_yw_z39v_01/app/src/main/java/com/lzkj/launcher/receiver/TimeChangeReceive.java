package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.util.AppUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年3月10日 下午4:47:04
 * @parameter 监听时间改变, 并且守护心跳进程
 */
public class TimeChangeReceive extends BroadcastReceiver {

    private static final String TAG = "TimeChangeReceive";

    /**
     * 心跳进程的名称
     */
    private static final String HEART_SERVICE_NAME = "com.lzkj.aidlservice.api.heart.HeartService";
    /**
     * 上传进程的名称
     */
    private static final String UPLOAD_SERVICE_NAME = "com.lzkj.downloadservice:upload";
    /**
     * 同步服务进程的名称
     */
    private static final String SYNC_SERVICE_NAME = "com.lzkj.aidlservice.service.SyncService";
    /**
     * 推送服务进程的名称
     */
    private static final String PUSH_SERVICE_NAME = "com.igexin.sdk.PushService";

    @Override
    public void onReceive(final Context context, Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean heartIsRun = AppUtil.isServiceWork(context, HEART_SERVICE_NAME);
                if (!heartIsRun) {//心跳未运行 则马上启动心跳
                    Log.d(TAG, "TimeChangeReceive onReceive startHeartService.");
                    startHeartService(LauncherApp.getApplication().getApplicationContext());
                }
            }
        }).start();
    }

    /**
     * 启动心跳服务
     */
    public void startHeartService(Context context) {
        try {
            Log.d(TAG, "TimeChangeReceive startHeartService.");
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.lzkj.aidlservice",
                    "com.lzkj.aidlservice.api.heart.HeartService");
            intent.setComponent(component);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
