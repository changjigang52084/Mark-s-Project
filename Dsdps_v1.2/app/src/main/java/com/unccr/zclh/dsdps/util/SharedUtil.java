package com.unccr.zclh.dsdps.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.unccr.zclh.dsdps.app.DsdpsApp;

public class SharedUtil {

    private SharedPreferences sharedPreferences;
    private String sharedName = "dsdps";
    /**
     * 设备的授权key
     **/
    public static final String AUTHORIZE_KEY = "authorize";
    /**
     * 节目id key
     **/
    public static final String PRM_ID_KEY = "prmIdKey";
    /**
     * 当前正在下载的prm key
     **/
    public static final String CURRENT_PRM_KEY = "currentPrmKey";
    public static final String ON_HOUR_KEY = "onHour";
    public static final String ON_MINUTE_KEY = "onMinute";

    public static final String OFF_HOUR_KEY = "offHour";
    public static final String OFF_MINUTE_KEY = "offMinute";
    /**
     * 撤销节目回执的adpressDataPackage对象"
     */
    public static final String DELETE_PRM_ADPRESSDATAPACKAGE = "delete_prm_adpressDataPackage";
    /**
     * 设备的id key
     **/
    public static final String DEVICE_ID_KEY = "device_id";
    /**
     * 节目id program
     */
    public static final String PROGRAM_ID = "programId";
    /**
     * 显示的权重比key
     **/
    public static final String KEY_WEIGHT = "weight";
    /**
     * 定时开关机时间
     */
    public static final String WORK_TIMER = "work_timer";
    /**
     * 节目列表的id
     */
    public static final String PROGRAM_LIST_ID = "program_id";

    /**
     * 节目显示url列表
     */
    public static final String PROGRAMME= "programme";

    /**
     * 节目显示url列表
     */
    public static final String PROGRAMME_PLAY_OVER= "programme_play_over";
    /**
     * 模块一下发列表
     */
    public static final String MODULEONE_PLAY_NAMES= "moduleOne_play_names";

    /**
     * 模块四下发列表
     */
    public static final String MODULEFOUR_PLAY_NAMES= "moduleFour_play_names";


    /**
     * 模块四节目列表
     */
    public static final String PROGRAMME_FOUR= "moduleFour";

    /**
     * 记录app闪退次数key
     **/
    public static final String KEY_APP_ERROR_INDEX = "appErrorIndex";
    /**
     * 记录app上次闪退的时间key
     **/
    public static final String KEY_APP_ERROR_DATE = "appErrorDate";
    /**
     * 当前播放的节目key key
     **/
    public static final String KEY_CURRENT_PLAY_PRM = "currentPrmKey";
    /**
     * 错误的节目key key
     **/
    public static final String KEY_ERROR_PRM = "errorPrmKey";
    /**
     * 缓存本地最新的节目
     */
    public static final String CACHE_LOCAL_PRM = "cache_local_prm";

    /**
     * 下载urls来源
     */
    public static final String PROGRAM_SOURCE_TYPE = "url_type";

    public static boolean isProhibit = false;
    public static String downloadOverFlag = "downloadOverFlag";

    /**SDcard key key**/
//	public static final String KEY_SD_CARD_KEY = "sdcardPathKey";

    /**
     * 默认的权重比
     **/
    public static final float DEFAULT_WEIGHT = 0.667f;

    private SharedUtil() {
        sharedPreferences = DsdpsApp.getDsdpsApp().
                getSharedPreferences(sharedName, Context.MODE_PRIVATE);
    }

    public static SharedUtil newInstance() {
        return SharedUtilInstance.SHARED_UTIL;
    }

    private static class SharedUtilInstance {
        private static final SharedUtil SHARED_UTIL = new SharedUtil();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * 添加String类型的数据到shared里面保存
     *
     * @param key   键
     * @param value 值
     */
    public void setString(String key, String value) {
        if (!isEmpty(key, value)) {
            if (null == sharedPreferences) {
                sharedPreferences = DsdpsApp.getDsdpsApp().
                        getSharedPreferences(sharedName, Context.MODE_PRIVATE);
            }
            sharedPreferences.edit().putString(key, value).commit();
        }
    }

    /**
     * 获取string类型的数据
     *
     * @param key 键
     * @return 返回值，如果shared里面没有这个值则返回null
     */
    public String getString(String key) {
        if (null == key) {
            return null;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, "");
    }
    /**
     * 获取string类型的数据
     *
     * @param key 键
     * @return 返回值，如果shared里面没有这个值则返回str
     */
    public String getString(String key,String str) {
        if (null == key) {
            return null;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, str);
    }
    /**
     * 设置boolean值
     *
     * @param key   键
     * @param value 值
     */
    public void setBoolean(String key, boolean value) {
        if (null == key) {
            return;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 设置int值
     *
     * @param key   键
     * @param value 值
     */
    public void setInt(String key, int value) {
        if (null == key) {
            return;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 设置long值
     *
     * @param key   键
     * @param value 值
     */
    public void setLong(String key, long value) {
        if (null == key) {
            return;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putLong(key, value).commit();
    }

    /**
     * 获取boolean值
     *
     * @param key 键
     * @return 默认返回false
     */
    public boolean getBoolean(String key) {
        if (null == key) {
            return false;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * 获取int值
     *
     * @param key 键
     * @return 默认返回-1
     */
    public int getInt(String key) {
        if (null == key) {
            return -1;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, -1);
    }

    /**
     * 获取long值
     *
     * @param key 键
     * @return 默认返回-1
     */
    public long getLong(String key) {
        if (null == key) {
            return -1;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getLong(key, -1);
    }

    /**
     * 设置float
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setFloat(String key, float value) {
        if (null == key) {
            return false;
        }
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        return sharedPreferences.edit().putFloat(key, value).commit();
    }

    /**
     * 获取float类型
     *
     * @param key
     * @return
     */
    public float getFloat(String key) {
        if (null == key || "".equals(key)) {
            return DEFAULT_WEIGHT;
        }

        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }

        if (!sharedPreferences.contains(key)) {
            return DEFAULT_WEIGHT;
        }

        return sharedPreferences.getFloat(key, DEFAULT_WEIGHT);
    }

    /**
     * 判断是否为空，如果是空则返回true
     *
     * @param key
     * @param value
     * @return
     */
    private boolean isEmpty(String key, String value) {
        if (null == key || null == value) {
            return true;
        }
        return false;
    }

    /**
     * 清除当前保存在shared的数据
     */
    public void clearAll() {
        if (null == sharedPreferences) {
            sharedPreferences = DsdpsApp.getDsdpsApp().
                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().clear().commit();
        sharedPreferences = null;
    }

    /**
     * 根据key移除掉记录
     *
     * @param key key
     * @return true is remove success,other fail
     */
    public boolean removeKey(String key) {
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.edit().remove(key).commit();
        }
        return false;
    }
}
