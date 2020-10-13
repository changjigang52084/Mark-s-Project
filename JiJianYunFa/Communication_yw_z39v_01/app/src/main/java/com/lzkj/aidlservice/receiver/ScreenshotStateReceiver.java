package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.service.TimerSleepScreenService;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年10月8日 下午3:33:51
 * @parameter 屏幕状态监听 是否为亮屏
 */
public class ScreenshotStateReceiver extends BroadcastReceiver {
    private static final LogTag TAG = LogUtils.getLogTag(ScreenshotStateReceiver.class, true);

    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtils.d(TAG, "ScreenshotStateReceiver", "onReceive screen shot");
        //判断当前屏幕是否已经是在休眠的状态下
        //如果是休眠状态，启动一个定时器
        if (null != context) {
            startTimerSleepService(context);
        }
    }

    private void startTimerSleepService(Context context) {
        LogUtils.d(TAG, "ScreenshotStateReceiver", "startTimerSleepService");
        Intent timerSleepIntent = new Intent(context, TimerSleepScreenService.class);
        timerSleepIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(timerSleepIntent);
    }
}
