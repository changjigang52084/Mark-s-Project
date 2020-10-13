package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.unccr.zclh.dsdps.play.PlayActivity;

public class StopAppReceiver extends BroadcastReceiver {

    private static final String TAG = "StopAppReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"onReceive Dsdps stop now!");
        if(PlayActivity.getActivity() != null){
            PlayActivity.getActivity().finish();
        }else {
            Log.d(TAG,"onReceive activity is null");
        }
    }
}
