package com.lzkj.aidlservice.util;


/**
 * 常量类
 *
 * @author changkai
 */
public class Constant {

    /**dsplug 类型*/
//	public static final int SYSTEM_TYPE_DSPLUG = SystemConstant.SYSTEM_TYPE_DSPLUG.intValue();
    /**social 类型*/
//	public static final int SYSTEM_TYPE_SOCIAL = SystemConstant.SYSTEM_TYPE_DSPLUG_SOCIAL.intValue();

    /**
     * 默认的redis 授权信息
     */
//	public final static String REIDS_AUTHENTICATION = ConfigSettings.IFTEST ? "0" : "36ae75b6195711e5:01211988zhouZHI";
    //初始化流量
    public static final String INIT_FLOW_KEY = "initFlowKey";
    /**
     * 安抚视频的activity
     */
    public static final String ELEVATOR_APPEASE_ACT = "com.chinacnit.elevator.ui.activity.AppeaseVideoActivity";

    /**
     * adw一键报警apk的包名
     */
    public static final String ADW_CALL_PHONE_UI_PCK = "com.chinacnit.elevator";
    /**
     * 电梯apk的包名
     */
    public static final String ELEVATOR_UI_PCK = "com.wconnet.eerp";

    /**
     * 碧池单兵apk的包名
     */
    public static final String BICHIDANBING_UI_PCK = "org.easydarwin.easypusher";

    /**
     * download apk ResposeDownloadService class name
     **/
    public final static String RESPOSEDOWNLOADSERVICE_CLS = "com.lzkj.downloadservice.service.ResposeDownloadService";
    /**
     * download apk DownloadService class name
     **/
    public final static String DOWNLOADSERVICE_CLS = "com.lzkj.downloadservice.mall.service.DownloadService";
    /**
     * download apk package name
     **/
    public final static String DOWNLOADSERVICE_PKG = "com.lzkj.downloadservice";
    /**
     * download cancel download list action
     **/
    public final static String CANCEL_DOWNLOAD_ACTION = "com.lzkj.downloadservice.CANCEL_DOWNLOAD_LIST_ACTION";
    /**ui apk LoginResultService class name*/
//	public final static  String LOGINRESULTSERVICE_CLS =  "com.lzkj.ui.service.LoginResultService";
    /**
     * ctrl apk package name
     */
    public final static String CTRL_PKG = "com.lz.ds.ctrl";
    /**
     * mallposter ui apk package name
     */
    public final static String MALL_POSTER_UI_PKG = "com.nextation.mallposter";
    /**
     * mallposter ui apk play activity name
     */
    public final static String MALL_POSTER_UI_ACT = "com.nextation.mallposter.ui.PlayActivity";
    /**
     * ui apk package name
     */
    public final static String UI_PKG = "com.lzkj.ui";
    /**
     * errp apk package name
     */
    public final static String ERRP_UI_PKG = "com.wconnet.eerp";
    /**
     * ui apk play activity name
     */
    public final static String UI_ACT = "com.lzkj.ui.PlayActivity";
    /**
     * 设备信息的广播action
     **/
    public final static String DEVICE_INFO_ACTION = "com.lzkj.ui.receiver.DEVICE_INFO_ACTION";
    /**
     * ui apk 截图服务的类名
     */
    public final static String SCREENSHOTSERVICE_CLS = "com.lzkj.ui.service.ScreenshotService";

    /**
     * download apk 包名
     */
    public final static String DOWNLOAD_PKG = "com.lzkj.downloadservice";
    /**
     * download apk 解绑的类名
     */
    public final static String DOWNLOADUNBIND_CLS = "com.lzkj.downloadservice.service.UnbindService";
    /**
     * download apk 上传的类名
     **/
    public final static String UPLOADSERVICE_CLS = "com.lzkj.downloadservice.service.UploadService";
    /**
     * download apk  重启redis的action
     **/
    public final static String RETRY_REDIS_ACTION = "com.lzkj.downloadservice.RETRY_REDIS_ACTION";
    /***上传日志的服务***/
    public final static String UPLOAD_LOG_SERVICE_CLS = "com.lzkj.downloadservice.service.TimerUploadLogService";
    /***上传日志的服务ACTION***/
    public final static String UPLOAD_LOG_SERVICE_ACTION = "com.lzkj.downloadservice.action.TIMER_UPLOAD_ACTION";
    /**
     * ui apk 切换节目的action
     */
    public final static String NOTFIY_PRM_ACTION = "com.lzkj.ui.NOTIFY_PRM_ACTION";
    /**
     * ui apk 更新天气的action
     */
    public final static String LAUNCHER_UPDATE_WEATHER_ACTION = "com.lzkj.ui.receive.UPDATE_WEATHER";
    /**
     * ui apk 更新音量的action
     */
    public final static String LAUNCHER_UPDATE_VOLUME_ACTION = "com.lzkj.ui.receive.UPDATE_VOLUME";
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
     * 截屏的action
     */
    public final static String SCREEN_SHOTS_ACTION = "com.lzkj.aidlservice.action.SCREEN_SHOTS_ACTION";
    /**
     * Launcher向communication获取二维码信息的action
     */
    public final static String LAUNCHER_GET_QRCODE_INFO_ACTION = "com.lzkj.ui.receive.GET_QRCODE_INFO";
    /**
     * ui apk 撤销节目的action
     */
    public final static String NOTFIY_DELETE_PRM_ACTION = "com.lzkj.ui.receive.NOTFIY_DELETE_PRM_ACTION";
    /**
     * ui apk 汇报撤销节目的action
     */
    public final static String REPORT_DELETE_PRM_ACTION = "com.lzkj.ui.receive.REPORT_DELETE_PRM_ACTION";
    /**
     * 关闭播放应用的action广播
     **/
    public final static String STOP_APP_ACTION = "com.lzkj.control.app.STOP_APP_ACTION";
    /**
     * 解绑的action
     */
    public final static String UNBIND_ACTION = "com.lzkj.aidlservice.Unbind_ACTION";
    /**
     * FEED_DOG_ACTION
     */
    public static final String FEED_DOG_ACTION = "com.lzkj.guardservice.FEED_DOG";
    /**
     * REGISTER_GUARD_ACTION
     */
    public final static String REGISTER_GUARD_ACTION = "com.lzkj.guardservice.REGISTER_GUARD";
    /**
     * UNREGISTER_GUARD_ACTION
     */
    public final static String UNREGISTER_GUARD_ACTION = "com.lzkj.guardservice.UNREGISTER_GUARD";
    /**
     * UNREGISTER_GUARD_ACTION
     */
    public final static String VENDOR_SHIPMENT_ACTION = "com.yqhd.vendingmachinedemo.VENDOR_SHIPMENT";


    /**
     * launcher apk package name
     */
    public final static String LAUNCHER_PKG = "com.lzkj.launcher";
    /**
     * launcher apk LoginResultService class name
     */
    public final static String LOGINRESULTSERVICE_CLS = LAUNCHER_PKG + ".service.LoginResultService";
    /**
     * launcher apk 解绑服务的类名
     */
    public final static String LAUNCHER_UNBIND_CLS = LAUNCHER_PKG + ".service.UnbindService";
    /**
     * 更新did的action
     */
    public final static String UPDATE_DID_ACTION = "com.lzkj.action.DEVICE_ID_ACTION";
    /**
     * 更新devicekey的action
     */
    public final static String UPDATE_DEVICE_KEY_ACTION = "com.lzkj.action.DEVICE_KEY_ACTION";
    /**
     * 更新社交媒体数据的action
     */
    public final static String UPDATE_SOCIAL_DATA_ACTION = "com.lzkj.action.SYNC_SOCIAL_DATA_ACTION";
    /**
     * 更新消息服务器的action
     */
    public final static String UPDATE_MESSAGE_SERVER_ACTION = "com.lzkj.action.MESSAGE_SERVER_ACTION";
    /**
     * 接收测试推送的action
     */
    public static String RECEIVER_PUSH_ACTION = "com.lzkj.debug.RECEIVER_PUSH_ACTION";

    /**
     * 同步设备节目的action
     */
    public static final String SYNC_DEVICE_PROGRAM = "com.lzkj.action.SYNC_DEVICE_PROGRAM";

    /**
     * 游戏排行榜包名
     **/
    public static String GAME_TOP_PCK = "com.yqhd.gametop";
    /**
     * 游戏排行榜activity
     **/
    public static String GAME_TOP_ACT = "com.yqhd.gametop.MainActivity";

    /**
     * 售货机应用包名
     **/
    public static String VENDOR_APP_PCK = "com.yqhd.vendingmachinedemo";
    /**
     * 售货机应用activity
     **/
    public static String VENDOR_APP_ACT = "com.yqhd.vendingmachinedemo.ui.activity.QRCodeActivity";

    /**
     * 更新权重的action
     */
    public static String ACTION_UPDATE_WEIGHT = "com.yqkj.action.UPDATE_WEIGHT";
    /**
     * mallPoster 的显示权重
     */
    public static final String MALLPOSTER_WEIGHT = "mallPosterWeight";
    /**
     * eposter 的显示权重
     */
    public static final String EPOSTER_WEIGHT = "eposterWeight";


    /**
     * deviceId key
     **/
    public final static String DEVICEID_KEY = "deviceId";
    /**
     * devicekey key
     **/
    public final static String DEVICE_KEY_KEY = "deviceKey";
    /**
     * mallposter deviceId key
     **/
    public static final String KEY_MALL_DEVICEID = "mallDeviceId";
    /**
     * server key
     **/
    public final static String SERVER_KEY = "server";

    /**
     * utf-8
     */
    public final static String UTF_CODE = "UTF-8";

    /**
     * 定时同步天气的时间 30分钟
     */
    public static final int SYNC_WEATHER_TIME = 30 * 60 * 1000;

    /**HttpUtil**/
    /**
     * 连接超时的时间
     */
    public static final int CONNECT_TIME_OUT = 30000;
    /**
     * http 重连的间隔时间
     */
    public static final long RETRY_SLEEP_TIME = 2000;
    /**
     * reids 连接超时的时间
     */
    public static final int REDIS_CONNECT_TIME_OUT = 5000;
    /**
     * 授权状态的key
     **/
    public static final String AUTHORIZE_KEY = "authorize";
    /**
     * 授权结果信息的key
     **/
    public static final String AUTHORIZE_MESSAGE = "authorize_message";
    /**
     * 更新社交媒体样式的key
     **/
    public static final String SOCIAL_MODULE_KEY = "update_social_module_type";
    /**
     * 极光推送
     */
    public static final int JPUSH = 1;
    /**
     * 个推推送
     */
    public static final int IGETUI = 2;

    /***
     * 终端控制命令
     */
    /**
     * 设置重启命令
     */
    public final static int REBOOT = 1;
    /**
     * 设置关机命令
     */
    public final static int SHUTDOWN = 2;
    /**
     * 设置定时开关机命令
     */
    public final static int SWITCH_WORK_TIME = 3;
    /**
     * 设置截图命令
     */
    public final static int SCREENSHOT = 4;
    /**
     * 静默安装APP命令
     */
    public final static int SILENCE_INSTALL = 5;
    /**
     * 静默卸载APP命令
     */
    public final static int SILENCE_UNINSTALL = 6;
    /**
     * 同步服务器时间APP命令
     */
    public final static int SYCN_TIME = 7;
    /**
     * 关闭广告机APP命令
     */
    public final static int STOP_APP = 8;
    /**
     * 启动广告机APP命令
     */
    public final static int START_APP = 9;
    /**
     * 关闭开关机
     **/
    public final static int CLOSE_WORK_TIME = 10;

    /**
     * 获取intent带过来的控制命令
     */
    public final static String CMD = "cmd";
    /**
     * 控制app的action
     **/
    public final static String CTRL_ACTION = "com.lz.ds.ctrl.CMD_ACTION";

    /**
     * 视频区域
     */
    public static final int VIDEO_FRAGMENT = 2;
    /**
     * 图片区域
     */
    public static final int PIC_FRAGMENT = 1;
    /**
     * 微博区域
     */
    public static final int WEIBO_FRAGMENT = 3;
    /**
     * 恢复素材文件url列表
     **/
    public static final String RECOVERY_DAMAGE_FILE_LIST = "fileUrlList";
    /**
     * 取消下载素材文件列表
     **/
    public static final String CANCEL_DOWNLOAD_FILE_LIST = "cancelDownloadFileList";
    /***
     * 恢复损坏文件的action
     */
    public static final String RECOVERY_DAMAGE_FILE_ACTION = "com.lzkj.downloadservice.receiver.RECOVERY_DAMAGE_FILE_ACTION";
    /**
     * 下载APP
     **/
    public static final int DOWNLOAD_APP = 3;
    /**
     * 分隔符
     */
    public static final String SPLIT_APP_PATH = "##@@##";

    /**
     * 分隔符 +++
     */
    public static final String SPLIT_PATH = "+++";

    /**
     * logPath
     **/
    public static final String LOG_PATH = "logPath";

    public static final String UPDATE_LOG_ACTION = "com.lzkj.downloadservice.receiver.UPLOAD_LOG_ACTION";

    /**
     * 下载应用的包名+类名
     **/
    public static final String DOWNLOAD_SERVICE_CLS = DOWNLOAD_PKG + ".service.ResposeDownloadService";
    /**
     * 下载列表
     */
    public static final String DOWNLOAD_LIST = "download_list";
    /**
     * 追加下载
     **/
    public static final int APPEND_DOWNLOAD = 1;
    /**
     * 下载类型 1.追加下载，0.恢复下载
     **/
    public static final String DOWNLOAD_TYPE_KEY = "downloadType";

    public static long lastBreakTime = 0L;
}
