package com.lzkj.aidlservice.push.igetui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.aidlservice.push.HandlerPushCommand;
import com.lzkj.aidlservice.push.handler.GeTuiPushHandler;

/**
 * 个推接受传透消息的类
 *
 * @author changkai
 */
public class GeTuiReceiverMsg extends BroadcastReceiver {

    private static final String TAG = "GeTuiReceiverMsg";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "GeTuiReceiverMsg onReceive intent.getAction(): " + intent.getAction());
        HandlerPushCommand.getInstance().handlerPayLoadMes(intent, GeTuiPushHandler.class);
    }
}
