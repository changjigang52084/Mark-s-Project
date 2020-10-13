package com.unccr.zclh.dsdps.push.control;

import android.content.Intent;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.WorkTimesInfo;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.DeviceUtil;
import com.unccr.zclh.dsdps.util.VolumeUtil;
import com.unccr.zclh.dsdps.util.WorkTimeUtil;

public class DeviceCommandControl {

    private static final String TAG = "DeviceCommandControl";

    public DeviceCommandControl() {

    }

    /**
     * 重启机器
     *
     * @param
     */
    public void reboot() {
        Log.d(TAG,"DeviceCommandControl reboot.");
        DeviceUtil.rebootDevice();
    }

    /**
     * 待机
     *
     * @param
     */
    public void standby() {
        Log.d(TAG,"DeviceCommandControl standby.");
        //DeviceUtil.standbyDevice();
    }

    /**
     * 唤醒
     *
     * @param
     */
    public void wakeUp() {
        Log.d(TAG,"DeviceCommandControl wakeUp.");
        //DeviceUtil.wakeUpDevice();
    }

    /**
     * 关机
     *
     * @param
     */
    public void shutdown(){
        Log.d(TAG,"DeviceCommandControl shutdown.");
        DeviceUtil.shutdownDevice();
    }

    /**
     * 更新终端的声音大小
     *
     * @param volume
     */
    public void updateDeivceVolume(int volume) {
        VolumeUtil.setDeviceVolume(volume);
    }

    /**
     * 更新的终端工作时间
     *
     * @param workTimesInfo
     */
    public void updateWorkTiming(WorkTimesInfo workTimesInfo) {
        WorkTimeUtil.get().updateWorkTime(workTimesInfo);
    }

    /**
     * 执行上传截图的命令
     *
     * @param
     */
    public void uploadScreenshot() {
        Log.d(TAG, "device screentshot.");
        //发送广播，通知应用截屏
        Intent shotIntent = new Intent();
        shotIntent.setAction(Constants.SCREEN_SHOTS_ACTION);
        shotIntent.putExtra("intervalTime", 0);
        shotIntent.putExtra("shotNumber", 1);
        DsdpsApp.getDsdpsApp().sendBroadcast(shotIntent);
    }

}
