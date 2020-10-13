package com.unccr.zclh.dsdps.board.sx;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class SXPowerOnOffManager {
    private static String KEY_POWER_ENABLE = "key_power_enable";
    private static String KEY_POWERON = "key_poweron_time";
    private static String KEY_POWEROFF = "key_poweroff_time";
    /**重启action**/
    private static final String REBOOT_ACTION = "android.intent.action.reboot";
    /**关机action**/
    private static final String SHUTDOWN_ACTION = "android.intent.action.shutdown";
    /**关闭lcd,hdmi输出的action,并且会关闭声音**/
    private static final String CLOSE_SCREEN_ACTION = "android.action.adtv.sleep";
    /**开启lcd,hdmi输出的action**/
    private static final String WAKEUP_SCREEN_ACTION = "android.action.adtv.wakeup";
    /**
     * 设置定时开关机
     * @param groupNum
     * 			表示哪一天，取值范围为0－7,　默认值为0。　周天，周一到周六分别对应1－7。
     * @param index
     * 			每天对应的哪一组，取值范围为0-4，分别对应第１组到第５组。
     * @param group
     * 			设置定时开关机bean
     * @param mContext
     * 			上下文
     */
    public static boolean setPowerTimePref(int groupNum, int index, SXPowerOnOffGroup group, Context mContext) {
        boolean isSuccess = false;
        String key_power = KEY_POWER_ENABLE + groupNum + "_" + index;
        String key_start = KEY_POWERON + groupNum + "_" + index;
        String key_end = KEY_POWEROFF + groupNum + "_" + index;
        ContentResolver resolver = mContext.getContentResolver();
//        isSuccess = Settings.System.putString(resolver, key_power, String.valueOf(group.mEnable));
//        if (isSuccess) {
//            isSuccess = Settings.System.putString(resolver, key_start,
//                    String.format("%02d:%02d", group.mStartHour, group.mStartMin));
//        }
//        if (isSuccess) {
//            isSuccess = Settings.System.putString(resolver, key_end,
//                    String.format("%02d:%02d", group.mEndHour, group.mEndMin));
//        }
        return isSuccess;
    }

    /**
     * 重启设备
     * @param context
     * 			上下文对象
     */
    public static void rebootDevice(Context context) {
        openScreen(context);
        context.sendBroadcast(new Intent(REBOOT_ACTION));
    }

    /**
     * 关机
     * @param context
     * 			上下文对象
     */
    public static void shutdownDevice(Context context) {
        context.sendBroadcast(new Intent(SHUTDOWN_ACTION));
    }
    /**
     * 关闭屏幕输出,会关闭掉声音
     * @param context
     */
    public static void closeScreen(Context context) {
        context.sendBroadcast(new Intent(CLOSE_SCREEN_ACTION));
    }
    /**
     * 开启屏幕输出
     * @param context
     */
    public static void openScreen(Context context) {
        context.sendBroadcast(new Intent(WAKEUP_SCREEN_ACTION));
    }

    public void setPowerTimePref(int groupNum, int index, String start,
                                 String end, Context mContext) {
        String key_power = KEY_POWER_ENABLE + groupNum + "_" + index;
        String key_start = KEY_POWERON + groupNum + "_" + index;
        String key_end = KEY_POWEROFF + groupNum + "_" + index;
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putString(resolver, key_power, "true");
        Settings.System.putString(resolver, key_start, start);
        Settings.System.putString(resolver, key_end, end);
    }
}
