package com.lzkj.aidlservice.util;

import com.lzkj.aidlservice.util.LogUtils.LogTag;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年9月6日 下午2:58:58
 * @parameter http相关的配置
 */
public class HttpConfigSetting {

    private static final LogTag TAG = LogUtils.getLogTag(HttpConfigSetting.class.getSimpleName(), true);

    /**
     * 获取注册服务器地址
     *
     * @return
     */
    public static String getRegisterHostIp() {
        return getDsplugRegisterServer();
    }

    /**
     * 获取消息服务器地址
     *
     * @return
     */
    public static String getMessageHostIp() {
        return getDsplugMessageServer();
    }

    /**
     * @Title: getDsplugRegisterServer
     * @Description: TODO 获取线上Dsplug系统的注册服务器地址
     */
    private static String getDsplugRegisterServer() {
        return HttpConstants.YW_DSPLUG_REGISTER_SERVER;
    }

    /**
     * @Title: getDsplugMessageServer
     * @Description: TODO 获取线上Dsplug系统的消息服务器地址
     */
    private static String getDsplugMessageServer() {
        return HttpConstants.YW_DSPLUG_MESSAGE_SERVER;
    }

    /**
     * 保存注册服务器地址到SharedPreferences
     *
     * @param registerServer 从服务器同步到的registerServer
     */
    public static void saveRegisterServer(String registerServer) {
        LogUtils.d(TAG, "saveRegisterServer", "registerServer: " + registerServer);
        SharedUtil.newInstance().setString(SharedUtil.REGISTER_SERVER_ID, registerServer);
    }

    /**
     * 获取SharedPreferences中的注册服务器
     *
     * @return 返回注册服务器地址
     */
    public static String getRegisterServer() {
        String registerServers = SharedUtil.newInstance().getString(SharedUtil.REGISTER_SERVER_ID);
        LogUtils.d(TAG, "getRegisterServer", "registerServers: " + registerServers);
        if (StringUtil.isNullStr(registerServers)) {
            return HttpConstants.DEFAULT_REGISTER_SERVER;
        }
        return registerServers;
    }

    /**
     * Save heart server host
     *
     * @param heartServer
     */
    public static void saveHeartServer(String heartServer) {
        LogUtils.d(TAG, "saveHeartServer", "heartServer: " + heartServer);
        SharedUtil.newInstance().setString(SharedUtil.HEARTSERVER_ID, heartServer);
    }

    /**
     * 获取SharedPreferences中的心跳服务器
     *
     * @return
     */
    public static String getHeartServer() {
        String heartServers = SharedUtil.newInstance().getString(SharedUtil.HEARTSERVER_ID);
        LogUtils.d(TAG, "getHeartServer", "heartServers: " + heartServers);
        if (StringUtil.isNullStr(heartServers)) {
            return HttpConstants.DEFAULT_HEART_SERVER;
        }
        return heartServers;
    }

    /**
     * 保存消息服务器地址到SharedPreferences
     *
     * @param messageServers
     */
    public static void saveMessageServer(String messageServers) {
        LogUtils.d(TAG, "saveMessageServer", "messageServers: " + messageServers);
        SharedUtil.newInstance().setString(SharedUtil.MESSAGE_SERVER_ID, messageServers);
    }

    /**
     * 获取SharedPreferences中的消息服务器
     *
     * @return 返回消息服务器地址
     */
    public static String getMessageServer() {
        String messageServers = ConfigSettings.getString(SharedUtil.MESSAGE_SERVER_ID);
        LogUtils.d(TAG, "getMessageServer", "messageServers: " + messageServers);
        if (StringUtil.isNullStr(messageServers)) {
            return HttpConstants.DEFAULT_MESSAGE_SERVER;
        }
        return messageServers;
    }

    /**
     * 获取向服务端领取通讯秘钥的URL
     *
     * @return
     */
    public static String getLoseCommunicaionUrl() {
        String registerServer = getRegisterServer();
        LogUtils.d(TAG, "getLoseCommunicaionUrl", "registerServer: " + registerServer);
        return String.format(HttpConstants.RECOVERY_COMMUNICATION_KEY_URL, registerServer);
    }

    /**
     * 获取终端绑定信息的URL
     *
     * @return
     */
    public static String getRecoveryRegisterUrl() {
        String registerServer = getRegisterServer();
        LogUtils.d(TAG, "getRecoveryRegisterUrl", "registerServer: " + registerServer);
        return String.format(HttpConstants.RECOVERY_REGISTER_URL, registerServer);
    }

    /**
     * 获取心跳的URL
     *
     * @return
     */
    public static String getHeartUrl() {
        String heartServer = getHeartServer();
        LogUtils.d(TAG, "getHeartUrl", "heartServer: " + heartServer);
        return String.format(HttpConstants.HEART_URL, heartServer);
    }

    /**
     * 获取消息推送回执的URL
     *
     * @return
     */
    public static String getPushReceiptUrl() {
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getPushReceiptUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.PUSH_RECEIPT_URL, messageServer);
    }

    /**
     * 获取同步天气的URL
     *
     * @return
     */
    public static String getSyncWeatherUrl() {
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getSyncWeatherUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.SYNC_WEATHER_URL, messageServer);
    }


    /**
     * 获取同步终端更新节目的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getSyncUpdateDevicePrmUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getSyncDevicePrmUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getSyncUpdateDevicePrmUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.SYNC_UPDATE_PROGRAMlIST_URL, messageServer, deviceId);
    }

    /**
     * 获取同步app版本的URL
     *
     * @return
     */
    public static String getSyncAppVersionUrl() {
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getSyncAppVersionUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.SYNC_APP_VERSION_URL, messageServer);
    }

    /**
     * 获取同步终端节目的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getSyncDevicePrmUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getSyncDevicePrmUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getSyncDevicePrmUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.SYNC_PROGRAMlIST_URL, messageServer, deviceId);
    }


    /**
     * 获取同步终端信息的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getSyncDeviceUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getSyncDeviceUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getSyncDeviceUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.SYNC_DEVICE_INFO_URL, messageServer, deviceId);
    }

    /**
     * 获取汇报设备关机状态的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getReportDeviceShutdownUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getReportDeviceShutdownUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getReportDeviceShutdownUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.REPORT_DEVICE_SHUTDOWN_STATE_URL, messageServer, deviceId);
    }

    /**
     * 获取汇报设备开机状态的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getReportDeviceStartupUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getReportDeviceStartupUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getReportDeviceStartupUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.REPORT_DEVICE_STARTUP_STATE_URL, messageServer, deviceId);
    }

    /**
     * 获取汇报设备磁盘状态的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getReportDeviceStrogeUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getReportDeviceStrogeUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getReportDeviceStrogeUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.REPORT_DEVICE_STORAGE_STATE_URL, messageServer, deviceId);
    }

    /**
     * 获取汇报设备磁盘TF卡状态的URL
     *
     * @param deviceId 设备id
     * @return
     */
    public static String getReportDeviceTFUrl(String deviceId) {
        if (StringUtil.isNullStr(deviceId)) {
            LogUtils.w(TAG, "getReportDeviceStrogeUrl", " error: deviceId is null.");
            return null;
        }
        String messageServer = getMessageServer();
        LogUtils.d(TAG, "getReportDeviceStrogeUrl", "messageServer: " + messageServer);
        return String.format(HttpConstants.REPORT_DEVICE_TF_STATE_URL, messageServer, deviceId);
    }

}
