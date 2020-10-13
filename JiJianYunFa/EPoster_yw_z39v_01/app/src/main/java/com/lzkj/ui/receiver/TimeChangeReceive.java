package com.lzkj.ui.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.app.EPosterApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeChangeReceive extends BroadcastReceiver {

    private static final String TAG = "TimeChangeReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        checkIsDateChange(context);
    }

    /**
     * 判断是否到了日期变更的时间,如果是则发送日期变更的广播
     */
    private void checkIsDateChange(Context context) {
        String hour = getStringTimeToFormat("HHmm");
        Log.d("checkIsDateChange", "hour: " + hour);
        if(hour.contains("0800")){
            Log.d("checkIsDateChange", "hour is 0800 startEposter");
            startEposter(context);
        }
        if (hour.contains("1000")) {
            Log.d("checkIsDateChange", "hour is 2000 startEposter");
            startEposter(context);
        }
    }

    /**
     * 根据用户传入的时间格式返回对应的字符类型
     *
     * @param format 例如:yyyy-MM-dd
     * @return 2015-03-30
     */
    public static String getStringTimeToFormat(String format) {
        if (null == format || "".equals(format)) {
            format = "yyyy-MM-dd";
        }
        return new SimpleDateFormat(format, Locale.CHINA).format(new Date());
    }

    private void startEposter(Context context) {
        Log.d(TAG, "startEposer...");
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }
}
