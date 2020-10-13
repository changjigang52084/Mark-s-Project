package com.lzkj.aidlservice.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.baize.adpress.core.protocol.factory.AdpressProtocolPackageFactoryManager;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

import java.util.Locale;

/**
 * @author kchang changkai@lz-mr.com
 * @Description:配置类
 * @time:2016年4月15日 下午12:17:42
 */
@SuppressLint("DefaultLocale")
public class ConfigSettings {
    private static final LogTag TAG = LogUtils.getLogTag(ConfigSettings.class.getSimpleName(), true);

    public static boolean LOG_TAG = true;

    /**
     * 默认最大上限流量为1G 单位M, 值为-1则表示不限制流量
     */
    public static long DEFAULT_MAX_FOLW = -1;

    public static int CLIENT_TYPE = HttpConstants.YW;

    /***是否为内网测试 true表示内网*/
//	public static final boolean IFTEST = false;

    /***是否同时安装了dsplug和socail*/
    public static boolean isInstallDSplugAndSocail = false;

    public static String MAC_ADDRESS = null;

    /**
     * 存储路径
     */
    public static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static int SCREEN_HEIGHT = Util.getRealDisplayHeight();
    public static int SCREEN_WIDTH = Util.getRealDisplayWidth();
    public static Locale SYSTEM_LOCALE = Locale.CHINA;

    /**
     * log过期时间为5天
     **/
    public static int LOG_OVERDUE_DATE = -5;

    /**
     * 配置当前安装的app
     */
    public static int CURRENT_APP = HttpConstants.SOCIAL_APP;

    /**
     * 配置当前app运行环境
     */
    public static int CURRENT_ENVIRONMENT = HttpConstants.ONLINE;

    /**
     * 同步间隔时间(6分钟)
     */
    public static long SYCN_DELAY_MILLIS = 60 * 1000;

    /**
     * 判断客户端是否已经绑定
     *
     * @return true表示绑定，false没有绑定
     */
    public static boolean isClientValid() {
        String deviceId = getDid();
        Log.d("isClientValid","deviceId: " + deviceId);
        return !StringUtil.isNullStr(deviceId);
    }

    /**
     * get device_id
     *
     * @return
     */
    public static String getDid() {
        return SharedUtil.newInstance().getString(SharedUtil.DEVICE_ID); // 5443
    }

    /**
     * 获取终端Key
     *
     * @return
     */
    public static String getDeviceKey() {
        return SharedUtil.newInstance().getString(SharedUtil.DEVICE_KEY);
    }

    /**
     * 保存did
     *
     * @param did
     */
    public static void saveDeviceId(String did) {
        LogUtils.d(TAG, "saveDeviceId", " did: " + did);
        SharedUtil.newInstance().setString(SharedUtil.DEVICE_ID, did);
    }

    public static void saveDeviceKey(String deviceKey) {
        LogUtils.d(TAG, "saveDeviceKey", " key: " + deviceKey);
        SharedUtil.newInstance().setString(SharedUtil.DEVICE_KEY, deviceKey);
    }

    /**
     * 保存通讯秘钥
     *
     * @param communicationKey
     */
    public static void saveCommunicationKey(String communicationKey) {
        if (StringUtil.isNullStr(communicationKey)) {
            LogUtils.w(TAG, "saveCommunicationKey", " communicationKey  is null.");
            return;
        }
        LogUtils.d(TAG, "saveCommunicationKey", "communicationKey:" + communicationKey);
        SharedUtil.newInstance().setString(SharedUtil.COMMUNICATION_PUBLIC_KEY, communicationKey);
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        if (StringUtil.isNullStr(key)) {
            return null;
        }
        return SharedUtil.newInstance().getString(key);
    }


    /**
     * 获取同步心跳间隔时间
     *
     * @return
     */
    public static long getSycnHertTime() {
        return SharedUtil.newInstance().getLong(SharedUtil.HEART_TIME_KEY);
    }

    /**
     * 获取同步节目间隔时间
     *
     * @return
     */
    public static long getSycnPrmTime() {
        return SharedUtil.newInstance().getLong(SharedUtil.SYNC_PRM_TIME_KEY);
    }

    /**
     * 获取同步设备间隔时间
     *
     * @return
     */
    public static long getSycnDeviceTime() {
        return SharedUtil.newInstance().getLong(SharedUtil.SYNC_DEVICE_TIME_KEY);
    }

    /**
     * 设置心跳时间
     *
     * @param hertTime 心跳间隔时间
     * @return 返回执行是否成功 true表示成功,false表示失败
     */
    public static boolean setSycnHertTime(long hertTime) {
        return SharedUtil.newInstance().setLong(SharedUtil.HEART_TIME_KEY, hertTime);
    }

    /**
     * 设置同步节目间隔时间
     *
     * @param sycnPrmTime 同步节目间隔时间
     * @return 返回执行是否成功 true表示成功,false表示失败
     */
    public static boolean setSycnPrmTime(long sycnPrmTime) {
        return SharedUtil.newInstance().setLong(SharedUtil.SYNC_PRM_TIME_KEY, sycnPrmTime);
    }

    /**
     * 设置同步设备间隔时间
     *
     * @param sycnDeviceTime 同步设备间隔时间
     * @return 返回执行是否成功 true表示成功,false表示失败
     */
    public static boolean setSycnDeviceTime(long sycnDeviceTime) {
        return SharedUtil.newInstance().setLong(SharedUtil.SYNC_DEVICE_TIME_KEY, sycnDeviceTime);
    }


    /**
     * 获取极光推送的id
     *
     * @return
     */
    public static String getRegistrationId() {
        return SharedUtil.newInstance().getString(SharedUtil.REGISTRATION_ID);
    }

    /**
     * 获取个推的client_id
     *
     * @return
     */
    public static String getClientId() {
        return SharedUtil.newInstance().getString(SharedUtil.CLIENT_ID);
    }

    /**
     * 获取通讯秘钥
     *
     * @return
     */
    public static String getCommunicationKey() {
        return SharedUtil.newInstance().getString(SharedUtil.COMMUNICATION_PUBLIC_KEY);
    }

    /**
     * 初始化通讯的工具包
     */
    public static void initAdpressCore() {
        AdpressProtocolPackageFactoryManager.init();
        PushUitl.initGeTuiPush();
        PushUitl.initJPush();
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static Long getVersionCode() {
        return SharedUtil.newInstance().getLong(SharedUtil.VERSION_CODE_KEY);
    }

    /**
     * 设置版本号
     *
     * @return
     */
    public static boolean setVersionCode(int versionCode) {
        return SharedUtil.newInstance().setLong(SharedUtil.VERSION_CODE_KEY, versionCode);
    }

    /**
     * 获取本机流量上限
     *
     * @return
     */
    public static long getMaxFolw() {
        long maxFolw = SharedUtil.newInstance().getLong(SharedUtil.MAX_FOLW_KEY);
        if (0 == maxFolw) {
            maxFolw = DEFAULT_MAX_FOLW;
        }
        return maxFolw;
    }

    /**
     * 获取使用的流量值
     *
     * @return
     */
    public static long getUsedFolw() {
        long usedFolw = SharedUtil.newInstance().getLong(SharedUtil.USE_FOLW_KEY);
        return usedFolw;
    }

    /**
     * 清除server
     *
     * @return
     */
    public static boolean clearServer() {
        SharedUtil.newInstance().removeKey(SharedUtil.REGISTER_SERVER_ID);
        SharedUtil.newInstance().removeKey(SharedUtil.HEARTSERVER_ID);
        SharedUtil.newInstance().removeKey(SharedUtil.WORK_SERVER_ID);
        SharedUtil.newInstance().removeKey(SharedUtil.CACHE_LOCAL_DEVICE_INFO);
        return SharedUtil.newInstance().removeKey(SharedUtil.MESSAGE_SERVER_ID);
    }
}
