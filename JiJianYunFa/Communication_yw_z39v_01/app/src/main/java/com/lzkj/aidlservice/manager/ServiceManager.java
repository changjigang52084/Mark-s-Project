package com.lzkj.aidlservice.manager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.api.heart.HeartService;
import com.lzkj.aidlservice.api.report.UploadAppInfoRunnable;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.handler.OverdueFileHandler;
//import com.lzkj.aidlservice.service.SowingAdvertisementService;
import com.lzkj.aidlservice.service.StorageSpaceService;
import com.lzkj.aidlservice.service.SyncService;
import com.lzkj.aidlservice.service.TimerSleepScreenService;
import com.lzkj.aidlservice.service.WeatherService;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;
import com.lzkj.aidlservice.util.WorkTimeUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年4月14日 下午10:01:31
 * @parameter 服务管理类
 */
public class ServiceManager {
    private static final LogTag TAG_LOG = LogUtils.getLogTag(ServiceManager.class.getSimpleName(), true);

    /**
     * 启动service
     **/
    private boolean startService = true;

    private ServiceManager() {
    }

    public static final ServiceManager getInstance() {
        return ServiceManagerInstance.INSTANCE;
    }

    private static class ServiceManagerInstance {
        private static final ServiceManager INSTANCE = new ServiceManager();
    }

    /**
     * 启动所有服务(HeartService,SyncService,SyncWeather,StorageSpaceService,TimerUploadLogService)
     */
    public void startAllService() {
        startHeartService();
        startSyncService();
        startTimerSleepScreenService();
        startSyncWeather();
//        startSowingAdvertisementService();
        startOrClosePlay(true);
        startStorageSpaceService();
        startGameTop();
        sendUpdateDeviceIdReceive(ConfigSettings.getDid());
        sendMessageServerReceive(HttpConfigSetting.getMessageServer());
        startOrStopTimerUploadLogService(startService);
        new OverdueFileHandler().delOverdueFile();
        WorkTimeUtil.get().startLocalWorkTime();
        reportAppInfoToService();
    }


    /**
     * 停止所有服务(HeartService,SyncService,SyncWeather,StorageSpaceService,TimerUploadLogService)
     */
    public void stopAllService() {
        stopHeartService();
        stopSyncService();
        stopTimerSleepScreenService();
//        stopSowingAdvertisementService();
        stopSyncWeather();
        startOrClosePlay(false);
        stopStorageSpaceService();
        sendUpdateDeviceIdReceive("");
        startOrStopTimerUploadLogService(!startService);
    }

    /**
     * 开机启动播放的activity
     *
     * @param isStartPlayApp true启动播放应用， false关闭播放应用
     */
    private void startOrClosePlay(boolean isStartPlayApp) {
        if (isStartPlayApp) {
            checkInstallWhichPlay();
        } else {
            Intent closeEposterIntent = new Intent(Constant.STOP_APP_ACTION);
            CommunicationApp.get().sendBroadcast(closeEposterIntent);
        }
    }

    /**
     * 启动游戏排行榜
     */
    private void startGameTop() {
        boolean isInstallGameTop = AppUtil.isInstallApp(CommunicationApp.get(), Constant.GAME_TOP_PCK);
        if (isInstallGameTop) {
            ComponentName component = new ComponentName(Constant.GAME_TOP_PCK, Constant.GAME_TOP_ACT);
            Intent gameTopIntent = new Intent();
            gameTopIntent.setComponent(component);
            gameTopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommunicationApp.get().startActivity(gameTopIntent);
        }
    }

    /**
     * 启动售货机应用
     */
    private void startVendorApp() {
        LogUtils.e(TAG_LOG, "startVendorApp", "startVendorApp");
        boolean isInstallVendorApp = AppUtil.isInstallApp(CommunicationApp.get(), Constant.VENDOR_APP_PCK);
        if (isInstallVendorApp) {
            LogUtils.e(TAG_LOG, "startVendorApp", "isInstallVendorApp:" + isInstallVendorApp);
            ComponentName component = new ComponentName(Constant.VENDOR_APP_PCK, Constant.VENDOR_APP_ACT);
            Intent vendorIntent = new Intent();
            vendorIntent.setComponent(component);
            vendorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommunicationApp.get().startActivity(vendorIntent);
        }
    }

    /**
     * 判断安装了哪个播放的软件
     */
    private void checkInstallWhichPlay() {
        if (ConfigSettings.isInstallDSplugAndSocail) {
            startWhichPlay(Constant.UI_PKG, Constant.UI_ACT);
            startWhichPlay(Constant.MALL_POSTER_UI_PKG, Constant.MALL_POSTER_UI_ACT);
            return;
        }

        switch (ConfigSettings.CURRENT_APP) {
            case HttpConstants.DSPLUG_APP:
                LogUtils.d(TAG_LOG, "checkInstallWhichPlay", "start DSPLUG_APP");
                startWhichPlay(Constant.UI_PKG, Constant.UI_ACT);
                break;
            case HttpConstants.SOCIAL_APP:
                LogUtils.d(TAG_LOG, "checkInstallWhichPlay", "start SOCIAL_APP");
                startWhichPlay(Constant.MALL_POSTER_UI_PKG, Constant.MALL_POSTER_UI_ACT);
                break;
            default:
                break;
        }
    }


    /**
     * 根据传入的包名和类名 启动对应的播放应用
     *
     * @param pkg 包名
     * @param cls 完整类名
     */
    private void startWhichPlay(final String pkg, final String cls) {
        if (!AppUtil.isInstallApp(CommunicationApp.get(), pkg)) {//判断是否安装了应用
            LogUtils.e(TAG_LOG, "startWhichPlay", "app not installed");
            return;
        }

        if (AppUtil.isRunningForeground(CommunicationApp.get(), pkg)) {
            LogUtils.d(TAG_LOG, "startWhichPlay", pkg + " allready start...");
            return;
        }
        CommunicationApp.get().mAppHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ComponentName component = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(component);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CommunicationApp.get().startActivity(intent);
            }
        }, 5000);
        LogUtils.d(TAG_LOG, "startWhichPlay", "start " + pkg);

    }

    /**
     * 启动or停止定时上传日志的service
     *
     * @param isStartService true启动，false停止服务
     */
    private void startOrStopTimerUploadLogService(boolean isStartService) {
        ComponentName component = new ComponentName(Constant.DOWNLOAD_PKG, Constant.UPLOAD_LOG_SERVICE_CLS);
        Intent timerLogIntent = new Intent(Constant.UPLOAD_LOG_SERVICE_ACTION);
        timerLogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        timerLogIntent.setComponent(component);

        if (isStartService) {
            CommunicationApp.get().startService(timerLogIntent);
        } else {
            CommunicationApp.get().stopService(timerLogIntent);
        }
    }

    /**
     * 发送更新deviceId的广播
     *
     * @param deviceId 最新的did
     */
    public void sendUpdateDeviceIdReceive(String deviceId) {
        if (!StringUtil.isNullStr(deviceId)) {
            Intent intent = new Intent(Constant.UPDATE_DID_ACTION);
            intent.putExtra(Constant.DEVICEID_KEY, deviceId);
            CommunicationApp.get().sendBroadcast(intent);
        }
    }

    /**
     * 发送更新deviceKey的广播
     *
     * @param deviceKey 终端key
     */
    public void sendUpdateDeviceKeyReceive(String deviceKey) {
        if (!StringUtil.isNullStr(deviceKey)) {
            Intent intent = new Intent(Constant.UPDATE_DEVICE_KEY_ACTION);
            intent.putExtra(Constant.DEVICE_KEY_KEY, deviceKey);
            CommunicationApp.get().sendBroadcast(intent);
        }
    }

    /**
     * 发送更新消息服务器的广播
     *
     * @param messageServer 最新的消息服务器地址
     */
    public void sendMessageServerReceive(String messageServer) {
        Intent intent = new Intent(Constant.UPDATE_MESSAGE_SERVER_ACTION);
        intent.putExtra(Constant.SERVER_KEY, messageServer);

        CommunicationApp.get().sendBroadcast(intent);
    }

    /**
     * 启动心跳服务
     */
    private void startHeartService() {
        startOrStopServiceToCls(HeartService.class, startService);
    }

    /**
     * ֹͣ停止心跳服务
     */
    private void stopHeartService() {
        startOrStopServiceToCls(HeartService.class, !startService);
    }

    /**
     * 启动同步服务
     */
    private void startSyncService() {
        startOrStopServiceToCls(SyncService.class, startService);
    }

    /**
     * ֹͣ停止同步服务
     */
    private void stopSyncService() {
        startOrStopServiceToCls(SyncService.class, !startService);
    }

    /**
     * 启动定时休眠服务
     */
    private void startTimerSleepScreenService() {
        startOrStopServiceToCls(TimerSleepScreenService.class, startService);
    }

    /**
     * 停止定时休眠服务
     */
    private void stopTimerSleepScreenService() {
        startOrStopServiceToCls(TimerSleepScreenService.class, !startService);
    }

//    /**
//     * 启动插播广告服务
//     */
//    private void startSowingAdvertisementService() {
//        startOrStopServiceToCls(SowingAdvertisementService.class, startService);
//    }

//    /**
//     * 停止插播广告服务
//     */
//    private void stopSowingAdvertisementService() {
//        startOrStopServiceToCls(SowingAdvertisementService.class, !startService);
//    }

    /**
     * 启动同步天气
     */
    private void startSyncWeather() {
        startOrStopServiceToCls(WeatherService.class, startService);
    }

    /**
     * ֹͣ停止同步服务
     */
    private void stopSyncWeather() {
        startOrStopServiceToCls(WeatherService.class, !startService);
    }

    /**
     * 启动磁盘汇报服务
     */
    private void startStorageSpaceService() {
        startOrStopServiceToCls(StorageSpaceService.class, startService);
    }

    /**
     * 停止磁盘汇报服务
     */
    private void stopStorageSpaceService() {
        startOrStopServiceToCls(StorageSpaceService.class, !startService);
    }

    /**
     * 根据类名启动或者停止service
     *
     * @param cls     要启动or停止服务的类名
     * @param isStart true启动服务，false停止服务
     */
    private void startOrStopServiceToCls(Class<? extends Service> cls, boolean isStart) {
        Context context = CommunicationApp.get();
        Intent serviceIntent = new Intent(context, cls);
        serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isStart) {
            context.startService(serviceIntent);
        } else {
            context.stopService(serviceIntent);
        }
    }

    /**
     * 向服务器汇报终端信息
     */
    private void reportAppInfoToService() {
        ThreadPoolManager.get().addRunnable(new UploadAppInfoRunnable());
    }
}
