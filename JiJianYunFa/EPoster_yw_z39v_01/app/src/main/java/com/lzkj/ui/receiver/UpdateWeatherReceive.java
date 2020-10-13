package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.SharedUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年5月6日 下午8:57:00
 * @parameter 更新天气的receive
 */
public class UpdateWeatherReceive extends BroadcastReceiver {
    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(UpdateWeatherReceive.class.getSimpleName(), true);
    /**
     * 缓存最新的天气信息
     */
    public static final String CACHE_WEATHER = "cache_weather";

    @Override
    public void onReceive(Context context, Intent intent) {
        String weatherResult = intent.getStringExtra(CACHE_WEATHER);
        LogUtils.d(TAG, "onReceive", "weather json:" + weatherResult);
        SharedUtil.newInstance().setString(CACHE_WEATHER, weatherResult);
    }
}
