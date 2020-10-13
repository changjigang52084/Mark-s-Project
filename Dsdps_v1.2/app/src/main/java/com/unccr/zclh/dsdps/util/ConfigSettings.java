package com.unccr.zclh.dsdps.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.github.mjdev.libaums.fs.UsbFile;
import com.unccr.zclh.dsdps.app.DsdpsApp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午5:38:4
 * @parameter ConfigSettings 区域
 */
public class ConfigSettings {

    /**
     * 当前app版本号
     **/
    public static final String DEVICE_NAME = null;
    public static final float SCALED_DENSITY = getScaledDensity();
    public static final int ANIMATION_COMPATIBLE_OFFSET = 2;
    public static int SCREEN_HEIGHT = LayoutUtil.getInstance().getRealDisplayHeight();
    public static int SCREEN_WIDTH = LayoutUtil.getInstance().getRealDisplayWidth();
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
    // 当前u盘所在文件目录
    public static UsbFile cFolder;
    public static boolean isCallPhone = false;
    public static boolean SIP_CONNECT_SERVER = false;
    public static boolean SIP_IS_LOGIN = false;
    public static final int MESSAGE_WHAT_UPDATE_LOCAL_VIDEO_ACCOUNT = 26;
    public static boolean SIP_THREAD_FLAG = true;
    public static final String SIP_THREAD_NAME = "SipThread";

    /**
     * app闪退最大数为5次 超过五次删除所有的节目 等待同步机制重新下载所有节目
     **/
    public static final int MAX_APP_ERROR_COUNT = 5;

    /***2个小时 单位毫秒*/
    public static final long MAX_APP_ERROR_INTERVAL_TIME = 7200000;

    public static boolean isOpenLog() {
        return true;
    }

    /**
     * get scaled density
     *
     * @return
     */
    public static float getScaledDensity() {
        Display d = ((WindowManager) DsdpsApp.getDsdpsApp().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        return dm.densityDpi;
    }

    /**
     * 保存prm key
     *
     * @param prmKey
     */
    public static void savePrmKey(Map<String, List<String>> prmKey) {
        SharedUtil.newInstance().setString(SharedUtil.PRM_ID_KEY, JSON.toJSONString(prmKey));
    }

    /**
     * 判断客户端是否已经绑定
     *
     * @return true表示绑定，false没有绑定
     */
    public static boolean isClientValid(){
        String deviceId = String.valueOf(getDeviceId());
        return !StringUtil.isNullStr(deviceId);
    }

    /**
     * 保存 deviceId
     */
    public static void saveDeviceId(int deviceId) {
        SharedUtil.newInstance().setInt(SharedUtil.DEVICE_ID_KEY, deviceId);
    }

    /**
     * 获取 deviceId
     *
     * @return
     */
    public static int getDeviceId() {
        return SharedUtil.newInstance().getInt(SharedUtil.DEVICE_ID_KEY);
    }

    /**
     * 保存 deviceId
     */
    public static void saveProgramId(int programId) {
        SharedUtil.newInstance().setInt(SharedUtil.PROGRAM_ID, programId);
    }

    /**
     * 获取 deviceId
     *
     * @return
     */
    public static int getProgramId() {
        return SharedUtil.newInstance().getInt(SharedUtil.PROGRAM_ID);
    }


    /**
     * 返回之前真正下载的节目id
     *
     * @return
     */
    public static String getCurrentPrmKey() {
        return SharedUtil.newInstance().getString(SharedUtil.CURRENT_PRM_KEY);
    }

    /**
     * 返回prm key map对象
     *
     * @return
     */
    public static Map<String, List<String>> getPrmKey() {
        String prmKey = SharedUtil.newInstance().getString(SharedUtil.PRM_ID_KEY);
        if (TextUtils.isEmpty(prmKey)) {
            return null;
        }
        Map<String, List<String>> prmKeyMap = (Map<String, List<String>>) JSON.parse(prmKey);
        return prmKeyMap;
    }

    /**
     * 保存当前正在下载的节目id
     *
     * @param prmKey 节目id
     */
    public static void saveCurrentPrmKey(String prmKey) {
        SharedUtil.newInstance().setString(SharedUtil.CURRENT_PRM_KEY, prmKey);
    }

}
