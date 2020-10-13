package com.unccr.zclh.dsdps.board.rxw;

import android.content.Intent;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.util.Cmd;


/**
 *
 * @version 1.0
 * @date 创建时间：2019年11月1日 上午09:22:17
 * @parameter 瑞芯微定时开关机接口
 */
public class RXWPowerOnOffManager {

    private static final String TAG = "RXWPowerOnOffManager";

    public boolean isSleep = false;
    private static volatile RXWPowerOnOffManager rxwPowerOnOffManager;

    public static RXWPowerOnOffManager get(){
        if(rxwPowerOnOffManager == null){
            synchronized (RXWPowerOnOffManager.class){
                if(rxwPowerOnOffManager == null){
                    rxwPowerOnOffManager = new RXWPowerOnOffManager();
                }
            }
        }
        return rxwPowerOnOffManager;
    }

    public void sleep() {
        isSleep = true;
        Log.d(TAG, "RXWPowerOnOffManager sleep.");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.gotosleep");
        DsdpsApp.getDsdpsApp().sendBroadcast(intent);
    }

    public void reboot(){
        isSleep = false;
        Cmd.rootCmd(GWPowerOnOffManager.REBOOT, null);
    }

    /**
     * 取消定时开关机
     */
//	public static void cancelPowerOnOff() {
//		Intent intent=new Intent();
//		intent.putExtra("enable", false);
//		intent.setAction("android.56iq.intent.action.setpoweronoff");
//		CommunicationApp.get().sendBroadcast(intent);
//	}
}
