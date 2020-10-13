package com.lzkj.aidlservice.api.sync;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.WeatherPackage;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年1月19日 下午3:33:14
 * @parameter 同步天气预报 定时30分钟刷新一次同步天气
 */
public class RequestSyncWeather implements IRequestCallback {

    private static final LogUtils.LogTag LOG_TAG = LogUtils.getLogTag(RequestSyncWeather.class.getSimpleName(), true);

    /**
     * 执行同步天气
     */
    public void syncWeather() {
        String did = ConfigSettings.getDid();
        if (StringUtil.isNullStr(did)) {
            LogUtils.d(LOG_TAG, "syncWeather", "device id null");
            return;
        }
        String requestParam = "deviceId=" + did;
        LogUtils.d(LOG_TAG, "syncWeather", "requestParam: " + requestParam);
        requestUpdateWeatherHttp(requestParam);
    }

    /**
     * 发送更新天气请求
     *
     * @param requestParm 请求的参数
     */
    private void requestUpdateWeatherHttp(String requestParm) {
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestCallback(this);
        httpRequestBean.setRequestUrl(HttpConfigSetting.getSyncWeatherUrl()); // 获取同步天气的url
        httpRequestBean.setRequestParm(requestParm); // 设置请求参数
        httpRequestBean.setRequestTag(RequestSyncWeather.class.getSimpleName());
        HttpUtil.newInstance().sendGetRequest(httpRequestBean);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        if (StringUtil.isNullStr(result)) {
            LogUtils.d(LOG_TAG, "onSuccess", "result is null");
            return;
        }
        try {
            // 获取到了最新天气，通知更新
            LogUtils.d(LOG_TAG, "onSuccess", "result: " + result);
            notifyUpdateWeather(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知更新天气
     *
     * @throws JSONException
     */
    private void notifyUpdateWeather(String weatherResult) throws JSONException {
        if (!StringUtil.isNullStr(weatherResult)) {
            JSONObject jsonObject = new JSONObject(weatherResult);
            String data = jsonObject.getString("data");
            LogUtils.d(LOG_TAG, "notifyUpdateWeather", "data: " + data);
            if (StringUtil.isNullStr(data)) {
                return;
            }
            WeatherPackage weatherPackage = JSON.parseObject(data, WeatherPackage.class);
            if (null == weatherPackage) {
                return;
            }
            if (StringUtil.isNullStr(weatherPackage.getWeather())) {
//				syncWeather();
                return;
            }

            if (StringUtil.isNullStr(weatherPackage.getWeatherCode())) {
//				syncWeather();
                return;
            }

            if (StringUtil.isNullStr(weatherPackage.getHigh())) {
//				syncWeather();
                return;
            }

            if (StringUtil.isNullStr(weatherPackage.getLow())) {
//				syncWeather();
                return;
            }
            weatherPackage.setCity(SharedUtil.newInstance().getString(SharedUtil.LOCATION_KEY));
        }
        //和本地的进行比较，判断是否相同数据如果相同则不做比较
        String cacheWeather = SharedUtil.newInstance().getString(SharedUtil.CACHE_WEATHER);
        //发送天气数据给展示数据的apk
        SharedUtil.newInstance().setString(SharedUtil.CACHE_WEATHER, weatherResult);
        sendNewWeather(weatherResult);
        LogUtils.d(LOG_TAG, "notifyUpdateWeather", "weatherResult: " + weatherResult);

    }

    public static void sendNewWeather(String weatherResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(weatherResult);
        String data = jsonObject.getString("data");
        WeatherPackage weatherPackage = JSON.parseObject(data, WeatherPackage.class);
//		weatherPackage.setCity(SharedUtil.newInstance().getString(SharedUtil.LOCATION_KEY));
        LogUtils.d(LOG_TAG, "sendNewWeather", "weatherPackage: " + JSON.toJSONString(weatherPackage));
        Intent updateWeatherIntent = new Intent();
        updateWeatherIntent.setAction(Constant.LAUNCHER_UPDATE_WEATHER_ACTION);// UI APK 更新天气的action
        updateWeatherIntent.putExtra(SharedUtil.CACHE_WEATHER, JSON.toJSONString(weatherPackage)); // cache_weather
        CommunicationApp.get().sendBroadcast(updateWeatherIntent); //  发送广播

    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        LogUtils.e(LOG_TAG, "onFaile", "errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
    }
}
