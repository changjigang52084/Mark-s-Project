package com.lzkj.aidlservice.util;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年1月19日 下午2:53:15
 * @parameter Http常量
 */
public class HttpConstants {
    /**
     * 线上
     **/
    public static final int ONLINE = 1;

    /**
     * yw
     */
    public static final int YW = 1;

    public final static String DMSS_URL = "mallappss-dmss";

    /**
     * 十分钟
     */
    public static final long TEN_MINUTE_LONG = 10 * 60 * 1000;

    /**
     * 五分钟
     */
    public static final long FIVE_MINUTE_LONG = 5 * 60 * 1000;
    /**
     * 一分钟
     */
    public static final long ONE_MINUTE_LONG = 1 * 60 * 1000;
    /**
     * 3分钟18w毫秒
     */
    public static final long THREE_MINUTE_LONG = 60 * 3 * 1000;


    /**
     * yw DSPLUG线上注册服务器
     **/
    public final static String YW_DSPLUG_REGISTER_SERVER = "www.jjyunfa.com:8080"; // 正式服务器
//    public final static String YW_DSPLUG_REGISTER_SERVER = "10.10.13.150:8080"; // 测试服务器
//    public final static String YW_DSPLUG_REGISTER_SERVER = "120.79.219.145:8080"; // 极简测试服务器

    /**
     * yw DSPLUG线上消息服务器
     **/
    public final static String YW_DSPLUG_MESSAGE_SERVER = "112.74.12.254:8080"; // 正式服务器
//    public final static String YW_DSPLUG_MESSAGE_SERVER = "10.10.13.150:8080"; // 测试服务器
//    public final static String YW_DSPLUG_MESSAGE_SERVER = "120.79.219.145:8080"; // 极简测试服务器


    /**
     * 默认的消息服务器地址
     */
    public final static String DEFAULT_MESSAGE_SERVER = HttpConfigSetting.getMessageHostIp();

    /**
     * 默认的心跳服务器地址
     */
    public final static String DEFAULT_HEART_SERVER = HttpConfigSetting.getMessageHostIp();
    /**
     * 默认的注册服务器地址
     */
    public final static String DEFAULT_REGISTER_SERVER = HttpConfigSetting.getRegisterHostIp();


//	//"192.168.0.124:8080" 
//	public final static String TEST_SERVER = "192.168.0.124:7070";
//	/**灰度发布服务器**/
//	public final static String DSPLUG_GREY_SERVER = "120.24.56.31:8080";
//	/**SOCIAL灰度发布服务器**/
//	public final static String DSPLUG_SOCIAL_GREY_SERVER = "120.24.56.31:8080";
//	
//	/**DSPLUG线上发布服务器**/
////	public final static String DSPLUG_SERVER = DSPLUG_GREY_SERVER;
//	public final static String DSPLUG_SERVER = "www.dsplug.com:8080";
//	
//	/**yw DSPLUG线上发布服务器**/
//	public final static String YW_DSPLUG_SERVER = "120.24.71.44:8080";
//	
//	/**SOCIAL线上发布服务器**/
////	public final static String DSPLUG_SOCIAL_SERVER = DSPLUG_GREY_SERVER;
//	public final static String DSPLUG_SOCIAL_SERVER = "www.dsplug.com:7070";
//	
//	/**DSPLUG线下默认注册服务器**/
//	public final static String TEST_REGISTER_SERVER = "192.168.0.124:7070";
//	
//	/**SOCIAL线下默认消息服务器**/
//	public final static String TEST_MALL_MESSAGE_SERVER = "192.168.0.124:9090";

//	/**
//	 * MALL默认的消息服务器地址
//	 */
//	public final static String MALL_DEFAULT_MESSAGE_SERVER = HttpConfigSetting.getMallMessageHostIP();
////	public final static String MALL_DEFAULT_MESSAGE_SERVER = HttpConfigSetting.IFTEST ? TEST_MALL_MESSAGE_SERVER : DSPLUG_SOCIAL_SERVER;
//	/**
//	 * MALL默认的注册服务器地址
//	 */
//	public final static String MALL_DEFAULT_REGISTER_SERVER = HttpConfigSetting.getMallRegisterHostIP();
////	public final static String MALL_DEFAULT_REGISTER_SERVER = HttpConfigSetting.IFTEST ? TEST_SERVER : DSPLUG_SOCIAL_SERVER;
//
//	
//	/**
//	 * 默认的消息服务器地址
//	 */
//	public final static String DEFAULT_MESSAGE_SERVER = HttpConfigSetting.getMessageHostIp();
////	public final static String DEFAULT_MESSAGE_SERVER = HttpConfigSetting.IFTEST ? TEST_SERVER : HttpConfigSetting.getAppHostIp();
//	
//	/**
//	 * 默认的工作服务器地址
//	 */
//	public final static String DEFAULT_WORK_SERVER = HttpConfigSetting.getServer();
////	public final static String DEFAULT_WORK_SERVER = HttpConfigSetting.IFTEST ? TEST_SERVER : HttpConfigSetting.getAppHostIp();
//	/**
//	 * 默认的心跳服务器地址
//	 */
//	public final static String DEFAULT_HEART_SERVER = HttpConfigSetting.getServer();
////	public final static String DEFAULT_HEART_SERVER = HttpConfigSetting.IFTEST ? TEST_SERVER : HttpConfigSetting.getAppHostIp();
//	/**
//	 * 默认的注册服务器地址
//	 */
//	public final static String DEFAULT_REGISTER_SERVER =  HttpConfigSetting.getServer();
////	public final static String DEFAULT_REGISTER_SERVER = HttpConfigSetting.IFTEST ? TEST_REGISTER_SERVER : HttpConfigSetting.getAppHostIp();

    /**
     * dsplug url版本
     */
    public final static String DSPLUG_URL_VERSION = "/v1";

    /**
     * social url版本
     */
    public final static String SOCIAL_URL_VERSION = "/v1";


    /**
     * dsplug app
     **/
    public static final int DSPLUG_APP = 1;
    /**
     * social app
     **/
    public static final int SOCIAL_APP = 2;

    /**
     * 注册服务器
     */
    public static final int REGISTER_SERVER = 0;
    /**
     * 消息服务器
     */
    public static final int MESSAGE_SERVER = 1;
    /**
     * 心跳服务器
     */
    public static final int HEART_SERVER = 2;


    /**新版本***/
    /**
     * 终端恢复注册的url
     */
    public static String RECOVERY_REGISTER_URL = "http://%s/" + DMSS_URL + DSPLUG_URL_VERSION + "/authorizations/devices/validations";
    /**
     * 终端获取通讯秘钥的url
     */
    public static String RECOVERY_COMMUNICATION_KEY_URL = "http://%s/" + DMSS_URL + DSPLUG_URL_VERSION + "/authorizations/devices/publicKey";
    /**终端绑定的url*/
//	public static  String REGISTER_URL = "http://%s/" + HttpConfigSetting.getAppUrl() + "/authorizations/devices";
    /**
     * 消息推送回执的url
     */
    public static String PUSH_RECEIPT_URL = "http://%s/mallappss-message/commands/receipts";
    /**
     * 心跳的URL
     */
    public static String HEART_URL = "http://%s/mallappss-message/heartbeat/beats";
    /**
     * 同步天气预报
     */
    public static String SYNC_WEATHER_URL = "http://%s/mallappss-message/synchronizations/weathers";
    /**
     * 同步更新节目列表的的URL deviceId = ;
     */
    public static String SYNC_UPDATE_PROGRAMlIST_URL = "http://%s/mallappss-message/synchronizations/programs/%s";
    /**
     * 同步更新节目列表的的URL deviceId = ;
     */
    public static String SYNC_PROGRAMlIST_URL = "http://%s/mallappss-message/synchronizations/programs/keys/%s";
    /**
     * 同步设备配置的URL; deviceid
     */
    public static String SYNC_DEVICE_INFO_URL = "http://%s/mallappss-message/synchronizations/devices/%s";
    /**
     * 汇报设备关机状态的URL
     */
    public static String REPORT_DEVICE_SHUTDOWN_STATE_URL = "http://%s/mallappss-message/reports/%s/shutdown";
    /**
     * 汇报设备开机状态的URL
     */
    public static String REPORT_DEVICE_STARTUP_STATE_URL = "http://%s/mallappss-message/reports/%s/startup";
    /**
     * 汇报设备存储状态的URL
     */
    public static String REPORT_DEVICE_STORAGE_STATE_URL = "http://%s/mallappss-message/reports/%s/storage";
    /**
     * 汇报设备TF卡存储状态的URL
     */
    public static String REPORT_DEVICE_TF_STATE_URL = "http://%s/mallappss-message/reports/%s/tfcard";
    /**
     * 汇报上传成功的URL POST请求
     * {
     * deviceId : 1,
     * screenShotUrl : "截屏图片的url"
     * }
     */
//	public static String REPORT_DEVICE_SCREENSHOT_URL = "http://%s/" + HttpConfigSetting.getAppUrl() + "/webservice/devices/screenshot";

    public static String REPORT_DEVICE_SCREENSHOT_URL = "http://%s/mallappss-message/reports/%s/screenshot";
    /**
     * 汇报终端工作信息的url
     **/
    public static String REPORT_DEVICE_WORK_URL = "http://%s/mallappss-message/reports/%s/workings";
    /**
     * 汇报售货机的工作信息的url
     **/
    public static String REPORT_VENDORS_WORK_URL = "http://%s/mallappss-message/reports/%s/vendors/workings";
    /***
     * 同步app版本号的url
     * POST
     * SyncDeviceApplicationVersionDto
     ***/
    public static String SYNC_APP_VERSION_URL = "http://%s/mallappss-message/synchronizations/devices/applications";
}
