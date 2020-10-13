package com.sunchip.adw.cloudphotoframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

public class NoOperationReceiver extends BroadcastReceiver {


    public NoOperationReceiver() {
    }

    public static NoOperationReceiver mNoOperationReceiver = new NoOperationReceiver();

    public static NoOperationReceiver getInstance(){
        if (mNoOperationReceiver==null){
            mNoOperationReceiver = new NoOperationReceiver();
        }
        return mNoOperationReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.e("NoOperationReceiver", "NoOperationReceiver===================" + intent.getAction());

    }

    public void mregisterReceiver(Context context) {
        Log.e("NoOperationReceiver","我注册了广播=============================");
        IntentFilter intentFilter = new IntentFilter();
//        com.xk.sendcommand.acttimeout
        intentFilter.addAction("com.xk.sendcommand.acttimeout");
        context.registerReceiver(this, intentFilter);
    }
}
