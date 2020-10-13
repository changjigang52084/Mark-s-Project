package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年3月16日 上午10:15:36
 * @parameter 安装app的广播
 */
public class AppInstallReceiver extends BroadcastReceiver {

    private static final String TAG = "AppInstall";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AppInstallReceiver onReceive action: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getDataString();
            Log.d(TAG, "AppInstallReceiver onReceive packageName: " + packageName);
        }
    }
}
