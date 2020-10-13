package com.lzkj.aidlservice.app;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.lzkj.aidlservice.bo.ConfigBo;
import com.lzkj.aidlservice.db.SQLiteHelper;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.manager.BaiduLocationManager;
import com.lzkj.aidlservice.manager.BaiduLocationService;
import com.lzkj.aidlservice.manager.ReportDeviceStatusManager;
import com.lzkj.aidlservice.manager.ServiceManager;
import com.lzkj.aidlservice.manager.ValidationDeviceManager;
import com.lzkj.aidlservice.receiver.TimeChangeReceive;
import com.lzkj.aidlservice.service.FeedDogService;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.DeviceFlowUtle;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StorageList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通讯的app
 *
 * @author changkai
 */
public class CommunicationApp extends Application {

    private static final String TAG = "CommunicationApp";

    private static CommunicationApp aidlApp;

    /**
     * 定位服务对象
     */
    public BaiduLocationService locationService;
    private static final int INIT_DAY = 1;
    public AppHandler mAppHandler = new AppHandler();
    private TimeChangeReceive changeReceive;

    public static CommunicationApp get() {
        return aidlApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        aidlApp = this;
        initSdcardPath();
        readConfig();
        initConfig();
        initServer();
        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                ConfigSettings.MAC_ADDRESS = FileUtile.getInstance().getMac();
                ValidationDeviceManager.get().validationDevice(ConfigSettings.MAC_ADDRESS, null);
            }
        });
    }

    /**
     * 初始化sd卡路径
     */
    private void initSdcardPath() {
        StorageList storageList = new StorageList(this);
        FileUtile.getInstance().setSDCard(storageList.getSDPath());
    }

    /**
     * 初始化server
     */
    private void initServer() {
        long versionCode = ConfigSettings.getVersionCode();
        PackageInfo packageInfo = AppUtil.getAppInfo(this, getPackageName());
        int currentVersionCode = packageInfo.versionCode;
        if (currentVersionCode <= 38) {
            //SQLiterManager.getInstance().delTableData(SQLiteHelper.TABLE_HTTP_REQUEST);
        }
//        if (versionCode != currentVersionCode) {
//            ConfigSettings.clearServer();
//            ConfigSettings.setVersionCode(currentVersionCode);
//            Log.d(TAG, "versionCode: " + versionCode + " ,currentVersionCode: " + currentVersionCode);
//        }
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        ConfigSettings.initAdpressCore();
        initLocation();
        if (ConfigSettings.isClientValid()) {
            //发送给服务器开机状态
            ReportDeviceStatusManager.get().reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_STARTUP);
            ServiceManager.getInstance().startAllService();
        }
        initUseFolw();
//		initFeedDog();
        registerTimeChangeReceive();
    }

    private void initUseFolw() {
        //初始化流量的月份
        int initFolwMonth = SharedUtil.newInstance().getInt(SharedUtil.INIT_FOLW_MONTH);
        if (initFolwMonth != getMonth() && getDay() == INIT_DAY) {
            //当前月份是否已经初始化流量
            SharedUtil.newInstance().setInt(SharedUtil.INIT_FOLW_MONTH, getMonth());
            SharedUtil.newInstance().setLong(SharedUtil.USE_FOLW_KEY, 0);
            DeviceFlowUtle.getInstance().sendUpdateMaxFlow(ConfigSettings.getMaxFolw(), true);
        }
    }

    /**
     * 获取当前的月份
     *
     * @return
     */
    private int getMonth() {
        int month = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        month = Integer.parseInt(dateFormat.format(new Date()));
        return month;
    }

    /**
     * 当前的月的第几日
     *
     * @return
     */
    private int getDay() {
        int day = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        day = Integer.parseInt(dateFormat.format(new Date()));
        return day;
    }

    /**
     * 读取配置信息
     */
    private void readConfig() {
        try {
            String configJson = FileUtile.getContentToInputStream(getAssets().open("config.json"));
            ConfigBo configBo = JSON.parseObject(configJson, ConfigBo.class);
            ConfigSettings.LOG_TAG = configBo.isLog();
            ConfigSettings.CURRENT_APP = configBo.getApp();
            ConfigSettings.CLIENT_TYPE = configBo.getApp_type();
            ConfigSettings.CURRENT_ENVIRONMENT = configBo.getRe();
            ConfigSettings.DEFAULT_MAX_FOLW = configBo.getFlow();
            ConfigSettings.LOG_OVERDUE_DATE = configBo.getLog_validity();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化爱迪威看门狗
     */
    private void initFeedDog() {
        Intent feedDogIntent = new Intent(getApplicationContext(), FeedDogService.class);
        feedDogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(feedDogIntent);
    }

    /**
     * 动态组成时间改变received
     */
    private void registerTimeChangeReceive() {
        if (null == changeReceive) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
            changeReceive = new TimeChangeReceive();
            registerReceiver(changeReceive, filter);
        }
    }

    /**
     * 初始化百度定位
     */
    private void initLocation() {
        locationService = new BaiduLocationService(getApplicationContext());//初始化定位sdk
        new BaiduLocationManager().startLocation();
    }

    public static class AppHandler extends Handler {
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
