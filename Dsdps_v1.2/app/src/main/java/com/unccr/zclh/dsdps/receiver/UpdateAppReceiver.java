package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.util.AppUtil;

public class UpdateAppReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateAppReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"action: " + action);
        AppUtil.startPlayActivity(DsdpsApp.getDsdpsApp());
    }
}
