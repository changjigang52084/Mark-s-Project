package com.sunchip.adw.cloudphotoframe.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.SunchipFile.File_Message;
import com.google.gson.Gson;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

public class SharedUtil {

    private SharedPreferences sharedPreferences;
    private String sharedName = "dsdps";

    private Context context;

    /**
     * 默认的权重比
     **/
    public static final float DEFAULT_WEIGHT = 0.667f;

    private SharedUtil() {
        sharedPreferences = CloudFrameApp.getCloudFrameApp().
                getSharedPreferences(sharedName, Context.MODE_PRIVATE);

        context = CloudFrameApp.getCloudFrameApp();
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
//            if (null == sharedPreferences) {
//                sharedPreferences = CloudFrameApp.getCloudFrameApp().
//                        getSharedPreferences(sharedName, Context.MODE_PRIVATE);
//            }
//            sharedPreferences.edit().putString(key, value).commit();

            File_Message.write(value, key, context);
        }
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
//        if (null == sharedPreferences) {
//            sharedPreferences = CloudFrameApp.getCloudFrameApp().
//                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
//        }
//        sharedPreferences.edit().putBoolean(key, value).commit();
        File_Message.write(value + "", key, context);
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
//        if (null == sharedPreferences) {
//            sharedPreferences = CloudFrameApp.getCloudFrameApp().
//                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
//        }
//        sharedPreferences.edit().putInt(key, value).commit();
        File_Message.write(value + "", key, context);
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
        boolean number = false;
        try {
            number = Boolean.parseBoolean(File_Message.read(key, context));
        } catch (Exception e) {
            Log.e("TAG", "无文件保存数据 都为false");
            return false;
        }
        return number;

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
//        if (null == sharedPreferences) {
//            sharedPreferences = CloudFrameApp.getCloudFrameApp().
//                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
//        }
//        return sharedPreferences.getString(key, "Null");

        String number = CloudFrameApp.getCloudFrameApp().
                getResources().getString(R.string.off);
        ;

        try {
            number = File_Message.read(key, context);
        } catch (Exception e) {
            Log.e("TAG", "无文件保存数据 都为NULL");
        }

        return number;

    }

    /**
     * 获取int值
     *
     * @param key 键
     * @return 默认返回0
     */
    public int getInt(String key) {
        if (null == key) {
            return 0;
        }
        int number = 0;
        try {
            number = Integer.parseInt(File_Message.read(key, context));
        } catch (Exception e) {
            Log.e("TAG", "无文件保存数据 都为0");
        }
        return number;
    }


    /**
     * 获取int值
     *
     * @param key 键
     * @return 默认返回0
     */
    public int getInt(String key, int value) {
        if (null == key) {
            return 0;
        }
//        if (null == sharedPreferences) {
//            sharedPreferences = CloudFrameApp.getCloudFrameApp().
//                    getSharedPreferences(sharedName, Context.MODE_PRIVATE);
//        }
//        return sharedPreferences.getInt(key, value);
        int number = value;

        try {
            number = Integer.parseInt(File_Message.read(key, context));
        } catch (Exception e) {
            Log.e("TAG", "无文件保存数据 都为0");
        }

        return number;
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
            sharedPreferences = CloudFrameApp.getCloudFrameApp().
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
