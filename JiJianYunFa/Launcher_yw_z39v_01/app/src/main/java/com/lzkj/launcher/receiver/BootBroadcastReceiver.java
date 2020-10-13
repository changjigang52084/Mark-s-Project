package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.MainActivity;
import com.lzkj.launcher.util.ConfigSettings;
import com.lzkj.launcher.util.Constant;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月2日 下午8:07:56
 * @parameter 拦截开机启动的广播
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "BootBroadcastReceiver onReceive action: " + intent.getAction());
        if (!ConfigSettings.isClientValid()) {
            Intent intents = new Intent(context, MainActivity.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
        } else {
            //启动心跳
            ComponentName componentName = new ComponentName(Constant.COMMUNICATION_PKG, Constant.COMMUNICATION_HEART_CLS);
            Intent hearIntent = new Intent();
            hearIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            hearIntent.setComponent(componentName);
            context.startService(hearIntent);
        }
    }
}
