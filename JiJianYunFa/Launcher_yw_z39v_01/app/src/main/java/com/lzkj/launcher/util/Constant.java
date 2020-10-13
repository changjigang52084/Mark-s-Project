package com.lzkj.launcher.util;

/**
 * 常量类
 *
 * @author changkai
 */
public class Constant {
    /**
     * download service apk包名
     **/
    public static final String DOWNLOAD_PKG = "com.lzkj.downloadservice";

    /**
     * communication apk包名
     **/
    public static final String COMMUNICATION_PKG = "com.lzkj.aidlservice";

    /**
     * eposter apk包名
     */
    public final static String UI_PKG = "com.lzkj.ui";

    /**
     * communication apk 登陆请求的类名
     */
    public static final String COMMUNICATION_LOGIN_CLS = "com.lzkj.aidlservice.service.LoginRequestService";
    /****心跳 类名****/
    public static final String COMMUNICATION_HEART_CLS = "com.lzkj.aidlservice.api.heart.HeartService";

    /**
     * 屏幕休眠类名
     **/
    public static final String COMMUNICATION_SLEEP_CLS = "com.lzkj.aidlservice.service.TimerSleepScreenService";

    /**
     * 快发云类名
     **/
    public static final String COMMUNICATION_SOWING_CLS = "com.lzkj.aidlservice.service.SowingAdvertisementService";

    /**
     * 关闭mall poster应用的广播
     */
    public static final String STOP_APP_ACTION = "com.lzkj.control.app.STOP_APP_ACTION";

    /**
     * ds-debug  apk包名
     **/
    public static final String DEBUG_PKG = "com.lzkj.debug";

    /**
     * ui apk play activity name
     */
    public final static String UI_ACT = "com.lzkj.ui.PlayActivity";


    /**
     * 登录的fragment
     */
    public static final int LOGIN_FRAGMENT = 1;
    /**
     * 所有应用桌面的fragment
     */
    public static final int ALL_APP_FRAGMENT = 2;

    /**
     * AES加密秘钥
     */
    public static final String AES_ENCRYP_KEY = "liangzhanmingrix";

    /**
     * 授权结果信息的key
     **/
    public static final String AUTHORIZE_MESSAGE = "authorize_message";

    /**
     * ui apk 更新城市定位的action
     */
    public final static String LAUNCHER_UPDATE_LOCATION_ACTION = "com.lzkj.ui.receive.UPDATE_LOCATION";
    /**
     * ui apk 更新推送id的action
     */
    public final static String LAUNCHER_UPDATE_PUSH_CLIENT_ACTION = "com.lzkj.ui.receive.UPDATE_PUSH_CLIENT";
    /**
     * ui apk 更新授权信息的action
     */
    public final static String LAUNCHER_UPDATE_AUTHORIZATION_ACTION = "com.lzkj.ui.receive.UPDATE_AUTHORIZATION";
    /**
     * 注册绑定的action
     **/
    public final static String UNBIND_ACTION = "com.lzkj.aidlservice.Unbind_ACTION";
    /**
     * 更新地址的action
     **/
    public final static String UPDATE_LOCATION_ACTION = "com.lzkj.ui.receive.UPDATE_LOCATION";
    /**
     * 更新授权的action
     **/
    public final static String UPDATE_AUTHORIZATION_ACTION = "com.lzkj.ui.receive.UPDATE_AUTHORIZATION";
    /**
     * Launcher向communication获取二维码信息的action
     */
    public final static String LAUNCHER_GET_QRCODE_INFO_ACTION = "com.lzkj.ui.receive.GET_QRCODE_INFO";
    /**
     * 检测终端上传检测报告的action
     */
    public static final String TEST_DEVICE_ACTION = "com.lzkj.debug.TEST_DEVICE_ACTION";
    /**
     * play播放软件信息
     */
    /**
     * 安装epost
     */
    public static final int EPOSTER = 1;

    /**
     * 版本号信息
     */
    /**
     * eposte app最新版本号versionCode
     */
    public static final String EPOSTER_VERSION = "87";
    /**
     * eposte 文件名
     */
    public static final String EPOSTER_FILE_NAME = "EPoster2.1.87.20201013.me.beta.apk";

    /**
     * download app最新版本号versionCode
     */
    public static final String DOWNLOAD_VERSION = "87";
    /**
     * download 文件名
     */
    public static final String DOWNLOAD_FILE_NAME = "DownloadService1.0.87.20201013.yw.beta.apk";

    /**
     * comm app最新版本号versionCode
     */
    public static final String COMM_VERSION = "87";
    /**
     * comm 文件名
     */
    public static final String COMM_FILE_NAME = "Communication1.0.87.20201013.m.beta.apk";

    /**
     * 上传日志的action
     **/
    public static final String UPLOAD_LOG_ACTION = "com.lzkj.downloadservice.receiver.UPLOAD_LOG_ACTION";
    /**
     * logPath
     **/
    public static final String LOG_PATH = "logPath";

    /***
     * 分割线
     */
    public static final String SPLIT = "|";
    /***
     * 错误日志开头分割线
     */
    public static final String ERROR_SPLIT = "#&#";

}
