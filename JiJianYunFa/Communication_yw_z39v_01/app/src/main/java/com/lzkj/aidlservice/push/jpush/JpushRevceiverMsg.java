package com.lzkj.aidlservice.push.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.aidlservice.push.HandlerPushCommand;
import com.lzkj.aidlservice.push.handler.JpushHandler;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月3日 下午8:36:18
 * @parameter 接收极光的推送消息
 */
public class JpushRevceiverMsg extends BroadcastReceiver {

    private static final String TAG = "JpushRevceiverMsg";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "JpushRevceiverMsg onReceive intent.getAction()= " + intent.getAction());
        HandlerPushCommand.getInstance().handlerPayLoadMes(intent, JpushHandler.class);
    }
}
