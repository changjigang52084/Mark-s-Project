package com.lzkj.ui.util;

/**
 * 常量类
 *
 * @author changkai
 */
public interface Constants {
    /***
     * 配置fresco图片缓存
     */
    static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();// 分配的可用内存
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;// 使用的缓存数量
//	public static final int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * ByteConstants.MB;// 默认图极低磁盘空间缓存的最大值
//	public static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;// 默认图低磁盘空间缓存的最大值
//	public static final int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;// 默认图磁盘缓存的最大值
    /**
     * 图片宽度
     */
    public final static int TARGET_WIDTH = 600;
    /**
     * 图片高度
     */
    public final static int TARGET_HEIGHT = 600;
    /**
     * 图片缓存的文件夹名称
     **/
    public static final String IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache";

    /**
     * 视频区域
     */
    public static final int VIDEO_FRAGMENT = 2;
    /**
     * 图片区域
     */
    public static final int PIC_FRAGMENT = 1;
    /**微博区域*/
//	public static final int WEIBO_FRAGMENT = 3;
    /**
     * 时间区域
     */
    public static final int DATE_FRAGMENT = 3;
    /**
     * 天气区域
     */
    public static final int WEATHER_FRAGMENT = 4;
    /**
     * 跑马灯区域
     */
    public static final int TEXT_FRAGMENT = 5;

    /**
     * PlayActivity 退出应用的广播action
     */
    public static final String EXIT_DOWNLOAD_ACTION = "com.lzkj.action.EXIT_ACTION";

    /**
     * communication apk包名
     **/
    public static final String COMMUNICATION_PKG = "com.lzkj.aidlservice";
    /**
     * communication apk 心跳类名
     */
    public static final String COMMUNICATION_HEART_CLS = "com.lzkj.aidlservice.heart.HeartService";
    /**
     * communication apk 登陆请求的类名
     */
    public static final String COMMUNICATION_LOGIN_CLS = "com.lzkj.aidlservice.service.LoginRequestService";
    /**
     * EPosterApp 守护广播action
     */
    public static final String COMMUNICATION_GUAR_ACTION = "com.lz.action.GUAR_RECEIVER";

    /**
     * 下载apk的包名
     */
    public static final String DOWNLOAD_PKG = "com.lzkj.downloadservice";
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
    /**
     * 下载文件
     */
    public static final int DOWNLOAD_FILE = 2;


    /**
     * 下载apk RecoveryDownloadTask的类名
     **/
    public static final String DOWNLOAD_RECOVERYDOWNLOADTASK_CLS = "com.lzkj.downloadservice.service.RecoveryDownloadTask";
    /**
     * 上传日志的action
     **/
    public static final String UPLOAD_LOG_ACTION = "com.lzkj.downloadservice.receiver.UPLOAD_LOG_ACTION";
    /**
     * 截屏的action
     */
    public final static String SCREEN_SHOTS_ACTION = "com.lzkj.aidlservice.action.SCREEN_SHOTS_ACTION";

    /**
     * com.lzkj.aidlservice.receiver.RequestSyncPrmReceiver广播拦截的action
     */
    public static final String REQUEST_SYNC_PRM_RECEIVER_ACTION = "com.lzkj.aidlservice.receiver.REQUEST_SYNC_PRM_ACTION";

    public static final String ENCODING = "UTF-8";
    /**
     * ctrl apk package name
     */
    public final static String CTRL_PKG = "com.lz.ds.ctrl";

    /**
     * mallposter apk package name
     */
    public final static String MALLPOSTER_PKG = "com.nextation.mallposter";

    /**
     * 接受上传的广播
     */
    public static final String SCREENTSHOT_UPLOAD_ACTION = "com.lzkj.action.UPLOAD_SCREENTSHOT_ACTION";

    /**
     * 接收关闭应用的广播
     */
    public static final String STOP_APP_ACTION = "com.lzkj.control.app.STOP_APP_ACTION";

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
     * ui apk 撤销节目的action
     */
    public final static String NOTFIY_DELETE_PRM_ACTION = "com.lzkj.ui.receive.NOTFIY_DELETE_PRM_ACTION";
    /**
     * ui apk 汇报撤销节目的action
     */
    public final static String REPORT_DELETE_PRM_ACTION = "com.lzkj.ui.receive.REPORT_DELETE_PRM_ACTION";
    /**
     * 更新天气的action
     */
    public final static String UPDATE_WEATHER_ACTION = "com.lzkj.aidlservice.action.UPDATE_WEATHER_ACTION";
    /**
     * 获取did的action
     */
    public final static String UPDATE_DID_ACTION = "com.nextation.mallposter.receive.GET_DEVICE_ID";
    /**
     * 天气apikey
     */
    public static final String WEATHER_API_KEY = "fd946debc0a78b70c88afebf38aadf60";
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

    /**
     * 恢复素材文件url列表
     **/
    public static final String RECOVERY_DAMAGE_FILE_LIST = "fileUrlList";

    /***
     * 恢复损坏文件的action
     */
    public static final String RECOVERY_DAMAGE_FILE_ACTION = "com.lzkj.downloadservice.receiver.RECOVERY_DAMAGE_FILE_ACTION";
    /**
     * 素材名称
     */
    public static final String MATERIAL_NAME = "materialName";
    /**
     * 素材类型
     */
    public static final String MATERIAL_TYPE = "materialType";
    /**
     * 素材MD5值
     */
    public static final String MATERIAL_MD5 = "materialMD5";
    /**
     * 节目key
     */
    public static final String PROGRAM_KEY = "programKey";

    /**
     * 文件前缀
     **/
    public static final String FILE_SCHEME = "file://";
    /**
     * activity name
     **/
    public static final String ACTIVITY_NAME = "activity";
    /**
     * 横屏节目
     **/
    static final int PROGREM_HORIZONTAL = 1;
    /**
     * 竖屏节目
     **/
    static final int PROGREM_VERTICAL = 2;
    /**
     * 温度单位
     **/
    public static final String TEMP_UNIT = "℃";
    /**
     * drawable
     **/
    public static final String DRAWABLE = "drawable";
    /**
     * 天气图标前缀
     **/
    public static final String WEATHER_ICON_PREFIX = "weather_state_icon_";

    public static float V_ADVERTISE_DEF_VOLUME = 0.1f;//广告页默认声音大小

}
