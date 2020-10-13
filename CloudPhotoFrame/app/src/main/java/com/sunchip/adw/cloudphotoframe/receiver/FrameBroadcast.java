package com.sunchip.adw.cloudphotoframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class FrameBroadcast extends BroadcastReceiver {

    private static  final String TAG = "FrameBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"action: " + intent.getAction());
//        Intent intent1 = new Intent(context,SplashActivity.class);
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent1);
    }
}
