package com.lzkj.aidlservice.adapter.board.rxw;

import android.content.Intent;
import android.util.Log;

import com.lzkj.aidlservice.app.CommunicationApp;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年5月24日 上午10:18:17
 * @parameter 瑞芯微定时开关机接口
 */
public class RXWPowerOnOffManager {

    private static final String TAG = "RXWPowerOnOffManager";

    public static void sleep() {
        Log.d(TAG, "RXWPowerOnOffManager sleep.");
        Intent intent = new Intent();
        intent.setAction("com.adw.android.broadcasttest.Poweroff");
        CommunicationApp.get().sendBroadcast(intent);
    }
}
