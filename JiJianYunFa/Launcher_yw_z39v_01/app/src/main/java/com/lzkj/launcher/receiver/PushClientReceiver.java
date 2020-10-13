package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.ShareUtil;
import com.lzkj.launcher.util.StringUtil;

public class PushClientReceiver extends BroadcastReceiver {

    private static final String TAG = "PushClientReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "PushClientReceiver onReceive Push Client intent is null.");
            return;
        }
        if (Constant.LAUNCHER_UPDATE_PUSH_CLIENT_ACTION.equals(intent.getAction())) {
            String getuiClientId = intent.getStringExtra(ShareUtil.GETUI_CLIENT_ID_KEY);
            Log.d(TAG, "PushClientReceiver onReceive getuiClientId: " + getuiClientId);
            if (!StringUtil.isNullStr(getuiClientId)) {
                ShareUtil.newInstance().setString(ShareUtil.GETUI_CLIENT_ID_KEY, getuiClientId);
            }
            String jpushClientId = intent.getStringExtra(ShareUtil.JPUSH_CLIENT_ID_KEY);
            Log.d(TAG, "PushClientReceiver onReceive jpushClientId: " + jpushClientId);
            if (!StringUtil.isNullStr(jpushClientId)) {
                ShareUtil.newInstance().setString(ShareUtil.JPUSH_CLIENT_ID_KEY, jpushClientId);
            }
        }
    }
}
