package com.unccr.zclh.dsdps.service.time;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.util.AppUtil;
import com.unccr.zclh.dsdps.util.DeviceUtil;
import com.unccr.zclh.dsdps.util.WorkTimeUtil;

import java.util.List;

public class TimerSleepScreenService extends Service {

    private static final String TAG = "TimerSleepScreenService";
    private static final long DELAY_MILLIS = 5 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        DsdpsApp.getDsdpsApp().mHandler.postDelayed(timerCheckScreenRunnable,DELAY_MILLIS);
    }

    private Runnable timerCheckScreenRunnable = new Runnable() {
        @Override
        public void run() {
            //判断当前是否为休眠时间段，并且没有在播放安抚视频和dsplug
            //如果在播放dsplug就发送关闭dsplug的广播，如果在播放安抚视频就等待下次检验的时间进行判断。
            DsdpsApp.getDsdpsApp().mHandler.postDelayed(this,DELAY_MILLIS);
            Log.d(TAG,"timerCheckScreenRunnable checkIsSleep isOpen: " + isOpen());
            if(checkIsSleep()){
                if(isOpen()){
                    Log.d(TAG,"timerCheckScreenRunnable checkIsSleep 休眠时间段内，机器处于开机状态，执行关机操作。");
                    handler.sendEmptyMessage(0);
                }
//                Log.d(TAG,"timerCheckScreenRunnable checkIsSleep is true. 休眠时间");
//                if(isOpen()){
//                    Log.d(TAG,"timerCheckScreenRunnable checkIsSleep 休眠时间段内，机器处于开机状态，执行关机操作。");
//                    Toast.makeText(DsdpsApp.getDsdpsApp(), "休眠时间段内，机器处于开机状态，30秒后执行关机操作", Toast.LENGTH_LONG).show();
//                    try {
//                        Thread.sleep(30*1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    DeviceUtil.shutdownDevice();
//                }
            }else{
                Log.d(TAG,"timerCheckScreenRunnable checkIsSleep is false. 工作时间");
            }
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Toast.makeText(DsdpsApp.getDsdpsApp(), "关机时间段内，机器处于开机状态，30秒后执行关机操作", Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(30*1000);
                        DeviceUtil.shutdownDevice();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    // 屏幕是否亮屏
    private boolean isOpen() {
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    /**
     * 判断当前时间是否为休眠时间段
     *
     * @return 返回true表示为休眠状态
     */
    private boolean checkIsSleep() {
        return WorkTimeUtil.get().checkCurrentTimeIsSleepTimeSlot();
    }

    private void closeScreen(){
//        GWPowerOnOffManager.powerOff();
    }

    /**
     * 关闭播放软件
     */
    private void closePlayApp(){
        AppUtil.stopApp("no poster app need to start.");
    }

    /**
     * 判断Dsdps是否在前台运行
     *
     * @return 返回true表示Eposter正在前台播放节目
     */
    private boolean checkIsPlay() {
        return AppUtil.isRunningForeground(DsdpsApp.getDsdpsApp(), "com.unccr.zclh.dsdps");
    }

    /**
     * 判断是否是在主屏
     *
     * @return 返回true表示在主屏
     */
    @TargetApi(Build.VERSION_CODES.Q)
    private boolean checkIsMainScreen() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        return tasks.get(0).baseActivity.getPackageName().contains("launcher");
    }



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy.");
    }
}
