package com.lzkj.aidlservice.adapter.board.gw;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.lzkj.aidlservice.adapter.board.rxw.RXWPowerOnOffManager;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.manager.ReportDeviceStatusManager;
import com.lzkj.aidlservice.service.FeedDogService;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.Cmd;
import com.lzkj.aidlservice.util.Cmd.CmdResultCallback;
import com.lzkj.aidlservice.util.Util;

import java.io.IOException;

public class GWPowerOnOffManager {

    private static final String TAG = "GWPowerOnOffManager";

    /**
     * 汇报设备开关机状态的广播
     */
    public final static String DEVICE_STARUP_SHUTDOWN_ACTION = "com.lz.action.device_shartup_shutdown";

    public static final String DEVICE_STATE_STARTUP = "on";
    public static final String DEVICE_STATE_SHUTDOWN = "off";
    public static final String REBOOT = "reboot";

    public static final String WATCH_DOG_PATH = "/sys/devices/platform/rk30_i2c.0/i2c-0/0-003c/adwdog";
    public static final String COMMAND = "b";

    private int off;
    private int on;
    private int remainOff;
    private int remainOn;

    private boolean isOn = true;

    private static final long SECOND = 1000l;
    private static final int SECOND_PER_MINUTE = 60;
    private Handler handler = null;

    public GWPowerOnOffManager() {
        handler = new Handler(CommunicationApp.get().getMainLooper());
    }

    /**
     * 设置开关机时间
     *
     * @param off 关机时间(单位分钟)
     * @param on  开机时间(单位分钟)
     */
    public void setOnOff(int off, int on) {
        if (off <= 0) {
            off = 1;
        }
        if (on <= 0) {
            on = 1;
        }
        this.off = off;
        this.on = on;
        resetRemain();
    }


    /**
     * 重置剩余时间
     */
    private void resetRemain() {
        remainOff = off * SECOND_PER_MINUTE;
        remainOn = on * SECOND_PER_MINUTE;
        handler.removeCallbacks(updateOnOffRunnable);
        handler.post(updateOnOffRunnable);
    }

    /**
     * 取消定时开关机
     */
    public void cancel() {
        Log.d(TAG, "cancel Cancel work time.");
        handler.removeCallbacks(updateOnOffRunnable);
        off = 0;
        on = 0;
        remainOff = 0;
        remainOn = 0;
    }

    private Runnable updateOnOffRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "updateOnOffRunnable isOn: " + isOn + " ,remainOff: " + remainOff + " ,remainOn: " + remainOn);
            handler.postDelayed(updateOnOffRunnable, SECOND_PER_MINUTE * SECOND);
            if (isOn) {
                remainOff -= SECOND_PER_MINUTE;
                if (remainOff < 0) {
                    remainOff = 0;
                    isOn = false;
                    powerOff();
                    handler.removeCallbacks(updateOnOffRunnable);
                    handler.post(updateOnOffRunnable);

                }
            } else {
                remainOn -= SECOND_PER_MINUTE;
                if (remainOn < 0) {
                    remainOn = 0;
                    isOn = true;
                    powerOn();
                }
            }
        }
    };

    /**
     * 关机
     */
    public void powerOff() {
        Log.d(TAG,"GWPowerOnOffManager powerOff.");
        RXWPowerOnOffManager.sleep();
        //发送给服务器待机状态
        ReportDeviceStatusManager.get().reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_SHUTDOWN);
        //关闭相关广告机app
        AppUtil.stopApp("no poster app need to start");
    }

    private CmdResultCallback cmdResultCallback = new CmdResultCallback() {
        @Override
        public void callback(String result, long timeConsume) {
            try {
                Log.d(TAG, "cmdResultCallback callback result: " + result);
                String state = checkDeviceState();
                Log.d(TAG, "cmdResultCallback state result: " + state);
                /**成功开关机*/
                if (DEVICE_STATE_SHUTDOWN.equals(state) || DEVICE_STATE_STARTUP.equals(state)) {
                    //发送广播告知Communication设备开关机状态
                    reportDeviceState(state);
                } else {
                    Log.e(TAG, "cmdResultCallback Error device state: " + state);
                }
            } catch (Exception e) {
                Log.e(TAG, "powerOff errMsg: " + e.getMessage());
            }

        }
    };

    /**
     * @return String 开机-"on"  关机-"off"
     * @throws IOException
     * @throws
     * @throws Exception
     * @Title: checkDeviceState
     * @Description: 检测终端开关机状态
     */
    private String checkDeviceState() {
        try {
            return Util.loadFileAsString("/sys/devices/platform/disp/hdmi_ctrl").trim();// 以前的代码获取开关机状态
        } catch (IOException e) {
            e.printStackTrace();
            return DEVICE_STATE_STARTUP;
        }
    }

    /**
     * @return boolean
     * @Title: isDeviceOnWhenWorkTimeSet
     * @Description: 判断重置开关机时间的时候，终端是否处于开机状态，如果不在开机状态则先重启终端
     */
    private boolean isDeviceOnWhenWorkTimeSet() {
        boolean isDeviceOnWhenWorkTimeSet = false;
        if (!DEVICE_STATE_STARTUP.equals(checkDeviceState())) {
            powerOn();
            isDeviceOnWhenWorkTimeSet = false;
        } else {
            isDeviceOnWhenWorkTimeSet = true;
        }
        return isDeviceOnWhenWorkTimeSet;
    }


    /**
     * 开机
     */
    private void powerOn() {
        //发送给服务器开机状态
        ReportDeviceStatusManager.get().reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_STARTUP);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"powerOn run.");
                Cmd.rootCmd(REBOOT,null);
            }
        }, 5 * SECOND);// 延时五秒后执行重启指令
    }

    /**
     * @Title: reboot
     * @Description: TODO 重启
     */
    public void reboot() {
        disableWatchDog();
        Cmd.rootCmd(REBOOT, null);
    }

    private void disableWatchDog() {
        Intent intent = new Intent(CommunicationApp.get().getApplicationContext(), FeedDogService.class);
        CommunicationApp.get().stopService(intent);
    }

    /**
     * 向Communication汇报设备状态
     *
     * @param
     * @return
     */
    private void reportDeviceState(String result) {
        Intent intent = new Intent();
        intent.setAction(DEVICE_STARUP_SHUTDOWN_ACTION);
        intent.putExtra("deviceState", result);
        CommunicationApp.get().getApplicationContext().sendBroadcast(intent);
    }

    public int getRemainOff() {
        return remainOff;
    }

    public int getRemainOn() {
        return remainOn;
    }
}
