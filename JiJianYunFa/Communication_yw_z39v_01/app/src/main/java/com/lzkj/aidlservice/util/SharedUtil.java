package com.lzkj.aidlservice.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/**
 * shared 的工具类
 *
 * @author changkai
 */
public class SharedUtil {

    private static final LogTag TAG_LOG = LogUtils.getLogTag(SharedUtil.class.getSimpleName(), true);
    private String sharedName = "pull_parm";

    /**
     * 最大流量限制
     */
    public static final String MAX_FOLW_KEY = "max_folw_key";
    /**
     * 已使用流量
     */
    public static final String USE_FOLW_KEY = "use_folw_key";
    /**
     * 初始化已使用流量
     */
    public static final String INIT_FOLW_KEY = "init_folw_key";
    /**
     * 初始化已使用流量的月份
     */
    public static final String INIT_FOLW_MONTH = "init_folw_month";

    public static final String ON_HOUR_KEY = "onHour";
    public static final String ON_MINUTE_KEY = "onMinute";

    public static final String OFF_HOUR_KEY = "offHour";
    public static final String OFF_MINUTE_KEY = "offMinute";

    /**
     * 当前应用版本号的key
     */
    public static final String VERSION_CODE_KEY = "version_code_key";

    /**
     * 个推的id
     */
    public static final String CLIENT_ID = "getui_client_id";
    /**
     * 极光推送的id
     */
    public static final String REGISTRATION_ID = "jpush_client_id";

    /**
     * 设备id
     */
    public static final String DEVICE_ID = "device_id";
    /**
     * 设备key
     */
    public static final String DEVICE_KEY = "device_key";

    /**
     * 包含了终端MAC或者机器码、终端唯一ID、授权唯一ID、终端绑定公司唯一ID和终端名称的加密信息
     */
    public static final String LICENSE_ID = "license";

    /**Eposter 消息服务器的key**/
    /**
     * 注册服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"
     */
    public static final String REGISTER_SERVER_ID = "register_server";
    /**
     * 心跳服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"
     */
    public static final String HEARTSERVER_ID = "heart_server";
    /**
     * 工作服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"
     */
    public static final String WORK_SERVER_ID = "work_server";
    /**
     * 消息服务器地址 "www.url1.com:8080"; "www.url2.com:8080"; "www.url3.com:8080"
     */
    public static final String MESSAGE_SERVER_ID = "message_server";

    /**
     * 通讯秘钥
     */
    public static final String COMMUNICATION_PUBLIC_KEY = "communication_key";
    /**
     * 定时开关机时间"
     */
    public static final String WROK_TIMER = "work_timer";

    /**
     * 截图回执的adpressDataPackage对象"
     */
    public static final String SCREENSHOT_ADPRESSDATAPACKAGE = "screenshot_adpressDataPackage";
    /**
     * 下载计划回执的adpressDataPackage对象"
     */
    public static final String DWONLOADPLAN_ADPRESSDATAPACKAGE = "download_panl_adpressDataPackage";
    /**
     * 重启回执的adpressDataPackage对象"
     */
    public static final String REBOOT_ADPRESSDATAPACKAGE = "reboot_adpressDataPackage";
    /**
     * 关机回执的adpressDataPackage对象"
     */
    public static final String SHUTDOWN_ADPRESSDATAPACKAGE = "shutdown_adpressDataPackage";
    /**
     * 工作时间回执的adpressDataPackage对象"
     */
    public static final String WORKTIME_ADPRESSDATAPACKAGE = "work_time_adpressDataPackage";
    /**
     * 安装应用回执的adpressDataPackage对象"
     */
    public static final String INSTALL_APP_ADPRESSDATAPACKAGE = "install_app_adpressDataPackage";
    /**
     * 撤销节目回执的adpressDataPackage对象"
     */
    public static final String DELETE_PRM_ADPRESSDATAPACKAGE = "delete_prm_adpressDataPackage";
    /**
     * 验证终端授权状态的adpressDataPackage对象"
     */
    public static final String VALIDATION_DEVICE_ADPRESSDATAPACKAGE = "validation_device_adpressDataPackage";
    /**
     * 售货机出货回执的adpressDataPackage对象"
     */
    public static final String VENDOR_SHIPMENT_ADPRESSDATAPACKAGE = "vendor_shipment_adpressDataPackage";

    /**
     * 是否使用七牛云存储
     */
    public static final String IS_QINIUYUN = "cloud_flag";
    /**
     * 节目列表的id
     */
    public static final String PROGRAM_LIST_ID = "program_id";
    /**
     * 缓存本地最新的节目
     */
    public static final String CACHE_LOCAL_PRM = "cache_local_prm";
    /**
     * 缓存本地最新的设备配置信息
     */
    public static final String CACHE_LOCAL_DEVICE_INFO = "cache_local_device";
    /**
     * 缓存mall本地最新的设备配置信息
     */
    public static final String CACHE_MALL_LOCAL_DEVICE_INFO = "cache_mall_local_device";
    /**
     * 缓存最新的天气信息
     */
    public static final String CACHE_WEATHER = "cache_weather";
    /**
     * 缓存最新的音量大小
     */
    public static final String CACHE_VOLUME = "cache_volume";
    /**
     * 保存位置的key
     **/
    public static final String LOCATION_KEY = "loctaion_key";
    /**
     * 保存心跳间隔时间的key
     **/
    public static final String HEART_TIME_KEY = "heart_time_key";

    /**
     * 保存同步节目的间隔时间的key
     **/
    public static final String SYNC_PRM_TIME_KEY = "sync_prm_time_key";
    /**
     * 保存同步设备间隔时间的key
     **/
    public static final String SYNC_DEVICE_TIME_KEY = "sync_device_time_key";


    private SharedUtil() {
    }

    private static SharedUtil sharedUtil = null;

    public static SharedUtil newInstance() {
        if (null == sharedUtil) {
            sharedUtil = new SharedUtil();
        }
        return sharedUtil;
    }

    /**
     * 设置key,value
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        if (null == key || null == value) {
            return;
        }
        LogUtils.d(TAG_LOG, "setString", "key is: " + key + " ,value: " + value);
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    /**
     * 获取string类型
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        if (null == key) {
            LogUtils.d(TAG_LOG, "getString", "key is null..");
            return null;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public long getLong(String key) {
        if (null == key) {
            return 0;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    public boolean setLong(String key, long value) {
        if (null == key) {
            return false;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        return preferences.edit().putLong(key, value).commit();
    }

    public boolean setInt(String key, int value) {
        if (null == key) {
            return false;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        return preferences.edit().putInt(key, value).commit();
    }

    public int getInt(String key) {
        if (null == key) {
            return -1;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }

    /**
     * 清除shared里面的数据
     */
    public void clearAll() {
        LogUtils.d(TAG_LOG, "clearAll", "clearAll");
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
        sharedUtil = null;
    }

    public boolean getBoolean(String key) {
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        if (null == key) {
            return;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 根据key移除掉shard里面的数据
     *
     * @param key
     * @return
     */
    public boolean removeKey(String key) {
        if (null == key) {
            return false;
        }
        SharedPreferences preferences = CommunicationApp.get().getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        if (preferences.contains(key)) {
            return preferences.edit().remove(key).commit();
        }
        return false;
    }
}

