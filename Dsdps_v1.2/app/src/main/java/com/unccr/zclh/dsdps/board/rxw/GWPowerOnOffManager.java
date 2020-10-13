package com.unccr.zclh.dsdps.board.rxw;

import android.os.Handler;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.util.Cmd;
import com.unccr.zclh.dsdps.util.DeviceUtil;
import com.unccr.zclh.dsdps.util.Util;

import java.io.IOException;

public class GWPowerOnOffManager {

    private static final String TAG = "GWPowerOnOffManager";
    /**汇报设备开关机状态的广播*/
    public final static String DEVICE_STARUP_SHUTDOWN_ACTION = "com.lz.action.device_shartup_shutdown";
    /************************************Z27关hdmi开hdmi命令******************************************************/
    public static final String CMD_POWER_OFF = "echo 1 > /sys/devices/platform/disp/hdmi_ctrl";
    public static final String CMD_POWER_ON = "echo 0 > /sys/devices/platform/disp/hdmi_ctrl";
    /************************************Z27关hdmi开hdmi命令******************************************************/

    /************************************Z12关hdmi开hdmi命令******************************************************/
//	public static final String CMD_POWER_OFF = "echo 1 > /sys/class/display/HDMI/enable";
//	public static final String CMD_POWER_ON = "echo 0 > /sys/class/display/HDMI/enable";
    /************************************Z12关hdmi开hdmi命令******************************************************/

    public static final String CMD_READ_HDMI = "cat /sys/devices/platform/disp/hdmi_ctrl";
    public static final String DEVICE_STATE_STARTUP = "on";
    public static final String DEVICE_STATE_SHUTDOWN = "off";
    public static final String REBOOT = "reboot";


    private int off;
    private int on;
    private int remainOff;
    private int remainOn;

    private boolean isOn = true;

    private static final long SECOND = 1000l;
    private static final int SECOND_PER_MINUTE = 60;
    private Handler handler = null;

    public GWPowerOnOffManager() {
        handler = new Handler(DsdpsApp.getDsdpsApp().getMainLooper());
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
//                    powerOff(); //休眠
                    DeviceUtil.shutdownDevice();
                    handler.removeCallbacks(updateOnOffRunnable);
                    handler.post(updateOnOffRunnable);

                }
            } else {
                remainOn -= SECOND_PER_MINUTE;
                if (remainOn <= 0) {
                    remainOn = 0;
                    isOn = true;
//                    powerOn(); // 唤醒
                    DeviceUtil.rebootDevice();
                }
            }
        }
    };

    /**
     * @Title: checkDeviceState
     * @Description: 检测终端开关机状态
     * @return String 开机-"on"  关机-"off"
     * @throws IOException
     * @throws
     * @throws Exception
     *
     */
    private String checkDeviceState() {
        try {
            return Util.loadFileAsString("/sys/devices/platform/disp/hdmi_ctrl").trim();
//			return Util.loadFileAsString("/sys/class/display/HDMI/enable").trim();
        } catch (IOException e) {
            e.printStackTrace();
            return DEVICE_STATE_STARTUP;
        }
    }

    /**
     * @Title: isDeviceOnWhenWorkTimeSet
     * @Description: 判断重置开关机时间的时候，终端是否处于开机状态，如果不在开机状态则先重启终端
     * @return boolean
     */
    private boolean isDeviceOnWhenWorkTimeSet(){
        boolean isDeviceOnWhenWorkTimeSet = false;
        if(!DEVICE_STATE_STARTUP.equals(checkDeviceState())){
            powerOn();
            isDeviceOnWhenWorkTimeSet = false;
        }else{
            isDeviceOnWhenWorkTimeSet = true;
        }
        return isDeviceOnWhenWorkTimeSet;
    }

    /**
     * 关机
     */
//    public static void powerOff() {
//        Log.d(TAG, "GWPowerOnOffManager powerOff.");
//        RXWPowerOnOffManager.get().sleep();
//        // 发送给服务器待机状态
//        SharedUtil.newInstance().setInt("state",2);
//        //关闭相关广告机app
//        AppUtil.stopApp("no poster app need to start");
//    }

    /**
     * 开机
     */
    private void powerOn() {
        // 发送给服务器开机状态
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "powerOn run.");
                Cmd.rootCmd(REBOOT,null);
            }
        }, 5 * SECOND);// 延时五秒后执行重启指令
    }

}
