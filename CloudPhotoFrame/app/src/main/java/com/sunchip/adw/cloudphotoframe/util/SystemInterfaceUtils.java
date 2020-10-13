package com.sunchip.adw.cloudphotoframe.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Locale;

import static android.content.Context.POWER_SERVICE;
import static cn.jiguang.dy.Protocol.mContext;

public class SystemInterfaceUtils {

    private String TAG = "SystemInterfaceUtils";


    public static SystemInterfaceUtils mSystemInterfaceUtils = new SystemInterfaceUtils();
    private static PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;


    public SystemInterfaceUtils() {
    }

    public static SystemInterfaceUtils getInstance() {
        if (mSystemInterfaceUtils == null) {
            mSystemInterfaceUtils = new SystemInterfaceUtils();
        }

        if (mPowerManager == null) {
            mPowerManager = (PowerManager) CloudFrameApp.getCloudFrameApp().getSystemService(POWER_SERVICE);
        }

        if (CloudFrameApp.policyManager == null) {
            CloudFrameApp.policyManager = (DevicePolicyManager) CloudFrameApp.getCloudFrameApp().getSystemService(Context.DEVICE_POLICY_SERVICE);
        }
        return mSystemInterfaceUtils;
    }


    //人体感应接口
    public void setPIR(boolean open, int leave_time) {
        //暂时时间是设置成三分钟 具体看系统的节点 /sys/module/rk_keys/parameters/pir_leave_time
        Log.e(TAG, "人体感应设置的状态是:" + open + " 设置的人体感应时间是: " + leave_time);

        PrintWriter fos = null;

        try {
            fos = new PrintWriter("/sys/module/rk_keys/parameters/pir_leave");
            if (open) {
                fos.println("1");
            } else {
                fos.println("0");
            }
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null)
                fos.close();
        }
    }


    @SuppressLint("InvalidWakeLockTag")
    public void setScreen(boolean open) {
        if (open) {
            // turn on screen
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            mWakeLock.acquire();
            mWakeLock.release();
        } else {
            //息屏
            CloudFrameApp.policyManager.lockNow();
        }
    }


    //设置时区 "America/Santiago"
    public void SetTimeZone(String TimeZone) {
        Log.e("TAG", "TimeZone:" + TimeZone);
        AlarmManager mAlarmManager = (AlarmManager) CloudFrameApp.getCloudFrameApp().getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone(TimeZone);
    }


    //按键控制
    public void SetAdbKeyevent(final String commend) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                CommandUtil.execRootCmdSilent(commend);
            }
        }.start();
    }


//    private long mKeyRemappingSendFakeKeyDownTime;
//
//    private void keyRemappingSendFakeKeyEvent(int action, int keyCode) {
//        long eventTime = SystemClock.uptimeMillis();
//        if (action == KeyEvent.ACTION_DOWN) {
//            mKeyRemappingSendFakeKeyDownTime = eventTime;
//        }
//
//
//        KeyEvent keyEvent = new KeyEvent(mKeyRemappingSendFakeKeyDownTime, eventTime, action, keyCode, 0);
//        InputManager inputManager = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
//        inputManager.injectInputEvent(keyEvent, 0);
//
//    }


    /**
     * 设置时间格式   format ="YYYYMMDD"   is24 是否为24小时制
     */
    public void setTime24(boolean is24) {
        boolean is24Hour = DateFormat.is24HourFormat(CloudFrameApp.getCloudFrameApp());
        Log.e("TAG", "设置的时间格式是:" + is24 + "   是否是24小时格式:" + is24Hour);

        String Value = "";
        try {
            if (is24)
                Value = "24";
            else
                Value = "12";
            Log.e("TAG", "Value:" + Value);
            Settings.System.putString(CloudFrameApp.getCloudFrameApp().getContentResolver(), Settings.System.TIME_12_24, Value);
        } catch (Exception e) {
            Log.e("TAG", "设置使用异常。。。。");
        }
    }

    //系统还原
    public void setFWRecovery(boolean clearuserdata) {
        Method systemProperties_get = null;
        String ret;
        try {
            systemProperties_get = Class.forName("android.os.RecoverySystem").getMethod("rebootWipeCache", Context.class, String.class);
            systemProperties_get.invoke(null, CloudFrameApp.getCloudFrameApp().getApplicationContext(),
                    CloudFrameApp.getCloudFrameApp().getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //语言设置
    public void setConfiguration(Activity activity, Locale loc, boolean IsStart) {
        // 获得res资源对象
        Resources resources = activity.getResources();
        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics metrics = resources.getDisplayMetrics();
        // 获得配置对象
        Configuration config = resources.getConfiguration();
        //区别17版本（其实在17以上版本通过 config.locale设置也是有效的，不知道为什么还要区别）
        //在这里设置需要转换成的语言，也就是选择用哪个values目录下的strings.xml文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(loc);
        } else {
            config.locale = loc;
        }
        resources.updateConfiguration(config, metrics);

        //是否需要重启界面刷新内容
        if (IsStart) {
            activity.finish();
            Intent intentPlay = new Intent(activity, activity.getClass());
            //清除activity的栈，来打开MainActivity看起来像是重启的效果。这个跳转过程要做到尽量的平滑
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intentPlay);
        }
    }
}
