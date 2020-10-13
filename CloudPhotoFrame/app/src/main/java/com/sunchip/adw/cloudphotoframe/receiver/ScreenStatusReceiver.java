package com.sunchip.adw.cloudphotoframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.ScreenStatu;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.playtimer;

//import static com.sunchip.adw.cloudphotoframe.InitializationActivity.Ismedia;

public class ScreenStatusReceiver extends BroadcastReceiver {

    String SCREEN_ON = "android.intent.action.SCREEN_ON";
    String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("TAG", "息屏的状态是:" + intent.getAction());

        int IsOpen = SharedUtil.newInstance().getInt("motionSensor");

        int vMensor = SharedUtil.newInstance().getInt("vMensor");

        if (SCREEN_ON.equals(intent.getAction())) {
            CloudFrameApp.IsScreen = true;
//            Ismedia = true;
            if (IsOpen == 1)
                SystemInterfaceUtils.getInstance().setPIR(true, 10);
            EventBus.getDefault().post(new BaseErrarEvent("", ScreenStatu));
        } else if (SCREEN_OFF.equals(intent.getAction())) {
            CloudFrameApp.IsScreen = false;
            if (vMensor == 1)
                SystemInterfaceUtils.getInstance().setPIR(false, 10);
        }
    }
}
