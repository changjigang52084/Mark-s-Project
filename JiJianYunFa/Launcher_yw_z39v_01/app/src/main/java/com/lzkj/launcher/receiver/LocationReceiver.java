package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.ShareUtil;

public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "LocationReceiver onReceive location.");
        if (intent == null) {
            return;
        }
        if (Constant.LAUNCHER_UPDATE_LOCATION_ACTION.equals(intent.getAction())) {
            String locationCity = intent.getStringExtra(ShareUtil.LOCATION_KEY);
            if (null != locationCity && !"".equals(locationCity)) {
                Log.d(TAG, "LocationReceiver onReceive locationCity: " + locationCity);
                ShareUtil.newInstance().setString(ShareUtil.LOCATION_KEY, locationCity);
            }
        }
    }
}
