package com.lzkj.ui.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.receiver.UpdateWeatherReceive;

import java.util.Locale;

/**
 * 配置类
 *
 * @author changkai
 */
public class ConfigSettings {

//    public static int countCompletion = 0;

    /**
     * 当前app版本号
     **/
    public static final String INNER_APK_VERSION = AppUtil.getVersion();
    public static final String DEVICE_NAME = null;
    public static final float SCALED_DENSITY = getScaledDensity();
    public static final int ANIMATION_COMPATIBLE_OFFSET = 2;
    public static int SCREEN_HEIGHT = LayoutUtil.getInstance().getRealDisplayHeight();
    public static int SCREEN_WIDTH = LayoutUtil.getInstance().getRealDisplayWidth();
//    public static String COUNT_URL_01 = "";
//    public static String COUNT_URL_02 = "";
//    public static String COUNT_URL_03 = "";
    public static String AD_KEY;
    public static int SHOW_TIME;
    /** 存储路径*/
    // public static String STORAGE_PATH = Environment. getExternalStorageDirectory().getAbsolutePath();

    /**
     * 播放U盘节目模式
     */
    public static boolean UDISK_MODE = false;
    /**
     * 显示天气
     */
    public static boolean SHOW_WEATHER = false;

    /**
     * 是否设置权重
     **/
    public static boolean IS_WEIGHT = false;

    public static Locale SYSTEM_LOCALE = Locale.CHINA;

    public static long VIEW_ID = 6;

    /**
     * app闪退最大数为5次 超过五次删除所有的节目 等待同步机制重新下载所有节目
     **/
    public static final int MAX_APP_ERROR_COUNT = 5;

    /***2个小时 单位毫秒*/
    public static final long MAX_APP_ERROR_INTERVAL_TIME = 7200000;

    public static String getDid() {
        return SharedUtil.newInstance().getString(SharedUtil.DEVICE_ID_KEY);
    }

    public static boolean isOpenLog() {
        return true;
    }

    /**
     * get scaled density
     *
     * @return
     */
    public static float getScaledDensity() {
        Display d = ((WindowManager) EPosterApp.getApplication().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        return dm.densityDpi;
    }

    /**
     * 设备是否绑定
     *
     * @return
     */
    public static boolean isClientValid() {
        return SharedUtil.newInstance().getBoolean(SharedUtil.AUTHORIZE_KEY);
    }

    /**
     * 获取天气
     *
     * @return
     */
    public static String getWeather() {
        return SharedUtil.newInstance().getString(UpdateWeatherReceive.CACHE_WEATHER);
    }
}
