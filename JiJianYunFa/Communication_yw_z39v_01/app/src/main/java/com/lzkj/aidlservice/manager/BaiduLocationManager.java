package com.lzkj.aidlservice.manager;

import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.lzkj.aidlservice.api.report.UploadLocationRunnable;
import com.lzkj.aidlservice.api.sync.RequestSyncWeather;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年1月20日 上午10:49:07
 * @parameter 定位管理
 */
public class BaiduLocationManager implements BDLocationListener {
    private static final LogTag TAG = LogUtils.getLogTag(BaiduLocationManager.class.getSimpleName(), true);

    public BaiduLocationManager() {
        CommunicationApp.get().locationService.registerListener(this);
    }

    /**
     * 启动定位,定位成功以后停止定位服务和注销监听
     */
    public void startLocation() {
        CommunicationApp.get().locationService.start();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLoction) {
        if (null != bdLoction) {
            String locationCity = bdLoction.getCity();
            String cityCode = bdLoction.getCityCode();
            LogUtils.d(TAG, "onReceiveLocation", "locationCity: " + locationCity + " ,cityCode: " + cityCode);
            handlerLocation(locationCity);
            unRegisterListener();
        }
    }

    private void handlerLocation(String locationCity) {
        if (StringUtil.isNullStr(locationCity)) {
            return;
        }
        notifyUpdateLoacation(locationCity);
        if (ConfigSettings.getDid() != null) {
            reportLocationToService();
        }
    }

    private void notifyUpdateLoacation(String locationCity) {
        SharedUtil.newInstance().setString(SharedUtil.LOCATION_KEY, locationCity);
        Intent updateLocationIntent = new Intent(Constant.LAUNCHER_UPDATE_LOCATION_ACTION);
        updateLocationIntent.putExtra(SharedUtil.LOCATION_KEY, locationCity);
        CommunicationApp.get().sendBroadcast(updateLocationIntent);
        //定位成功以后发送同步天气的命令
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                if (null != ConfigSettings.getDid()) {
                    new RequestSyncWeather().syncWeather();
                }
            }
        });
    }

    /**
     * @Description 发送定位到服务器
     */
    private void reportLocationToService() {
        ThreadPoolManager.get().addRunnable(new UploadLocationRunnable());
    }

    /**
     * 取消注册
     */
    private void unRegisterListener() {
        CommunicationApp.get().locationService.stop();
        CommunicationApp.get().locationService.unregisterListener(this);
    }

}
