package com.lzkj.launcher.postern;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lzkj.launcher.R;
import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.util.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 广告机后门管理器
 *
 * @author lyhuang
 * @date 2016-2-27 下午5:36:22
 */
public class PosternManager {

    private static final String TAG = "PosternManager";

    private static PosternManager ins;
    /**
     * 监听时间
     */
    private static final long LISTENER_TIME = 60 * 1000;
    /**
     * 延时开启后门
     */
    private static final long uptimeMillis = 3 * 1000;
    /**
     * handler
     */
    private Handler mHandler = null;
    /**
     * 后门开关监听计时器
     */
    private Timer mTimer = null;
    /**
     * 后门开关是否开启监听
     */
    private boolean isPosternSwitchListener = false;
    /**
     * 后门触发顺序
     */
    private static final String POSTERN_SWITCH_PW = R.id.top_left_window + "_" +
            R.id.top_right_window + "_" +
            R.id.bottom_right_window + "_" +
            R.id.bottom_left_window + "_";

    /**
     * 用户输入的开关按钮缓存
     */
    private StringBuilder posternKeyCache = null;

    private Runnable openPosternTask = null;

    /**
     * 后门开关监听任务：规定时间内没有开启后门，则关闭监听
     */
    private TimerTask listenerTask = null;

    private PosternManager(Runnable openPosternTask) {
        mTimer = new Timer();
        mHandler = new Handler();
        posternKeyCache = new StringBuilder();
        this.openPosternTask = openPosternTask;
    }

    public static PosternManager get(Runnable openPosternTask) {
        if (null == ins) {
            ins = new PosternManager(openPosternTask);
        }
        return ins;
    }

    /**
     * 开启后门监听
     *
     * @param
     * @return
     */
    public void openPosternListener() {
        closePosternListener();
        Log.d(TAG, "PosternManager openPosternListener.");
        isPosternSwitchListener = true;
        listenerTask = getTimerTask();
        mTimer.schedule(listenerTask, LISTENER_TIME);
    }

    /**
     * 关闭后门开关
     *
     * @param
     * @return
     */
    public void closePosternListener() {
        Log.d(TAG, "PosternManager closePosternListener.");
        isPosternSwitchListener = false;
        posternKeyCache.delete(0, posternKeyCache.length());
        if (listenerTask != null) {
            listenerTask.cancel();
        }
    }


    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                closePosternListener();
            }
        };
    }

    /**
     * 输入开启后门的key
     *
     * @param
     * @return
     */
    public void inputPosternKey(String key) {
        if (!isPosternSwitchListener()) {
            Log.i(TAG, "PosternManager inputPosternKey postern switch listener is closed.");
            return;
        }
        if (posternKeyCache == null) {
            Log.e(TAG, "PosternManager inputPosternKey Postern key cache is null.");
            return;
        }
        String keys = posternKeyCache.toString();
        if (keys.split("_").length >= getPosternSwitchPwLength()) {
            Log.w(TAG, "PosternManager inputPosternKey postern key overflow.");
            closePosternListener();
            return;
        }
        Log.d(TAG, "PosternManager inputPosternKey posternKey: " + key);
        posternKeyCache.append(key);
        posternKeyCache.append("_");
        keys = posternKeyCache.toString();
        Log.d(TAG, "PosternManager inputPosternKey posternKeyCache: " + posternKeyCache.toString());

        if (checkPosternSwitch(posternKeyCache.toString())) {
            openPostern();
            return;
        }
        if (keys.split("_").length >= getPosternSwitchPwLength()) {
            Log.i(TAG, "PosternManager inputPosternKey error postern key. Clear.");
            Toast.makeText(LauncherApp.getApplication()
                    , StringUtil.getString(R.string.error_postern_key), Toast.LENGTH_SHORT).show();
            closePosternListener();
        }
    }

    /**
     * 获取后门触发顺序的长度
     *
     * @param
     * @return 后门触发顺序的长度
     */
    private int getPosternSwitchPwLength() {
        return POSTERN_SWITCH_PW.split("_").length;
    }

    /**
     * 后门开关是否开启
     *
     * @param
     * @return boolean
     */
    public boolean isPosternSwitchListener() {
        return isPosternSwitchListener;
    }

    /**
     * 检测是否匹配后门开关
     *
     * @param posternWindowStr 触发后门开关的按钮顺序
     * @return boolean
     */
    public boolean checkPosternSwitch(String posternWindowStr) {
        if (POSTERN_SWITCH_PW.equals(posternWindowStr)) {
            return true;
        }
        return false;
    }

    /**
     * 开启后门
     */
    private void openPostern() {
        if (openPosternTask == null) {
            Log.w(TAG, "PosternManager openPostern open postern task is null.");
            return;
        }
        Log.i(TAG, "PosternManager openPostern open postern soon.");
        mHandler.postAtTime(openPosternTask, uptimeMillis);
        closePosternListener();
    }

    public void destory() {
        closePosternListener();
        mTimer.cancel();
        mTimer = null;
        mHandler.removeCallbacks(null);
        mHandler = null;
        ins = null;
    }
}
