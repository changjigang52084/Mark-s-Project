package com.lzkj.aidlservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.lzkj.aidlservice.api.sync.RequestSyncWeather;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年1月20日 上午11:16:44
 * @parameter 定时同步天气的服务
 */
public class WeatherService extends Service {

    private static final LogUtils.LogTag LOG_TAG = LogUtils.getLogTag(WeatherService.class.getSimpleName(), true);

    private WeatherHandler weatherHandler;

    private static final long DELAY_MILLIS = 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        weatherHandler = new WeatherHandler();
        CommunicationApp.get().mAppHandler.postDelayed(weatherRun, DELAY_MILLIS);
    }

    /**
     * 定时同步天气
     */
    private void timerSyncWeather() {
        weatherHandler.removeCallbacks(weatherRun);
        weatherHandler.postDelayed(weatherRun, Constant.SYNC_WEATHER_TIME);
    }

    private Runnable weatherRun = new Runnable() {
        @Override
        public void run() {
            new RequestSyncWeather().syncWeather();
            timerSyncWeather();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class WeatherHandler extends Handler {

    }
}
