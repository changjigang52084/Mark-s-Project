package com.lzkj.launcher.util;

import android.os.Environment;
import android.util.Log;

import java.util.Locale;

/**
 * 项目名称：Launcher
 * 类名称：ConfigSettings
 * 类描述：   配置类
 * 创建人："lyhuang"
 * 创建时间：2015年8月7日 上午11:53:31
 */
public class ConfigSettings {

    private static final String TAG = "ConfigSettings";

    public static final boolean LOG_TAG = false;

    public static String MAC_ADDRESS = null;

    public static int SCREEN_HEIGHT = LayoutUtil.getRealDisplayHeight();
    public static int SCREEN_WIDTH = LayoutUtil.getRealDisplayWidth();

    /**
     * 配置安装那个播放软件
     **/
    public static final int INSTALL_APP = Constant.EPOSTER;

    /**
     * 语言
     */
    public static byte LOCALE = 1;

    public static Locale SYSTEM_LOCALE = Locale.CHINA;

    /**
     * 存储路径
     */
    public static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 判断客户端是否已经绑定
     *
     * @return true表示绑定，false没有绑定
     */
    public static boolean isClientValid() {
        boolean isClientValid = ShareUtil.newInstance().getBoolean(ShareUtil.AUTHORIZE_KEY);
        Log.d(TAG, "ConfigSettings isClientValid isClientValid: " + isClientValid);
        return isClientValid;
    }

    /**
     * 获取定位城市
     */
    public static String getLocationCity() {
        String locationCity = ShareUtil.newInstance().getString(ShareUtil.LOCATION_KEY);
        Log.d(TAG, "ConfigSettings getLocationCity locationCity: " + locationCity);
        return locationCity;
    }

    /**
     * 获取个推id
     */
    public static String getGetuiClientId() {
        String getuiClientId = ShareUtil.newInstance().getString(ShareUtil.GETUI_CLIENT_ID_KEY);
        Log.d(TAG, "ConfigSettings getGetuiClientId getuiClientId: " + getuiClientId);
        return getuiClientId;
    }

    /**
     * 获取极光推id
     */
    public static String getJpushClientId() {
        String jpushClientID = ShareUtil.newInstance().getString(ShareUtil.JPUSH_CLIENT_ID_KEY);
        Log.d(TAG, "ConfigSettings getJpushClientId jpushClientID: " + jpushClientID);
        if (null == jpushClientID) {
            jpushClientID = "123456";
        }
        return jpushClientID;
    }
}
