package com.sunchip.adw.cloudphotoframe.Timer;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.tfsd.TimeS;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.play.PlayVideoPicActivity;

import org.greenrobot.eventbus.EventBus;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.NotDialogSystemUI;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.playtimer;

public class PlayTimer extends CountDownTimer {

    // 熄屏时间定时器
    private static String TAG = "MyCount";
    static PlayTimer myCount;

    /*
     * 主要是重写onTick和onFinsh这两个方法，onFinish()中的代码是计时器结束的时候要做的事情； onTick(Long
     * m)中的代码是你倒计时开始时要做的事情，参数m是直到完成的时间，
     * 构造方法MyCount()中的两个参数中，前者是倒计的时间数，后者是倒计时onTick事件响应的间隔时间，
     * 都是以毫秒为单位。例如要倒计时30秒，每秒中间间隔时间是1秒，两个参数可以这样MyCount(30000,1000)。
     * 将后台线程的创建和Handler队列封装成为了一个方便的类调用。 当你想取消的时候使用mc.cancel()方法就行了。
     */
    public PlayTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(final long millisUntilFinished) {

        //正在倒计时
    }

    @Override
    public void onFinish() {
        //倒计时结束
        Log.e(TAG, "========MyCount_onFinish()==========");
        EventBus.getDefault().post(new BaseErrarEvent("", playtimer));
    }

    // 直接一分钟倒计时
    public static void setStartMyCount() {
        if (myCount != null) {
            myCount.cancel();
        }
        myCount = new PlayTimer(TimeS.TIME_1, 1000);
        myCount.start();
    }

    // 可以设置时间的倒计时
    public static void setStartCount(long Time) {
        if (myCount != null) {
            myCount.cancel();
        }
        myCount = new PlayTimer(Time, 1000);
        myCount.start();
    }

    //关闭倒计时
    public static void setCloseMyCount() {
        if (myCount != null) {
            myCount.cancel();
        }
    }
}
