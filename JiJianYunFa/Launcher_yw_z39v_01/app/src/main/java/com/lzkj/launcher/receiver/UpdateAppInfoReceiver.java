package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.util.ShareUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年8月20日 下午1:08:50
 * @parameter 接受用户发送app信息的
 */
public class UpdateAppInfoReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateAppInfo";

    private static final String APP_INFO_ACTION = "com.lzkj.ui.APP_INFO_ACTION";
    private String versionCodeKey = "versionCode";
    private String versionNameKey = "version_name";
    private String appNameKey = "app_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "UpdateAppInfoReceiver onReceive action: " + intent.getAction());
        int code = intent.getIntExtra(versionCodeKey, -1);
        String vName = intent.getStringExtra(versionNameKey);
        String appName = intent.getStringExtra(appNameKey);
        Log.d(TAG, "UpdateAppInfoReceiver onReceive code: " + code + " ,vName: " + vName + " ,appName: " + appName);
        ShareUtil.newInstance().setInt(appName, code);
        ShareUtil.newInstance().setString(appName, vName);
    }
}
