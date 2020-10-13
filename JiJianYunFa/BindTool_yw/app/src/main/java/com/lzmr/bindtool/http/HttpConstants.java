package com.lzmr.bindtool.http;

/**
 * http url 常量类
 *
 * @author lyhuang
 * @date 2016-2-18 上午11:44:46
 */
public class HttpConstants {

	public final static String DSPLUG_URL = "iadpress";
	public final static String DMSS_URL = "mallappss-dmss";

	/**
	 * @Fields STATUS_CODE_SESSION_INVALID : TODO 网络连接异常
	 */
	public final static int STATUS_CODE_NETWORK_EXCEPTION = 0;

	/**
	 * @Fields STATUS_CODE_SESSION_INVALID : TODO 会话失效
	 */
	public final static int STATUS_CODE_SESSION_INVALID = 666;
	/**
	 * @Fields STATUS_CODE_SESSION_INVALID : TODO 网络连接异常
	 */
	public final static int STATUS_CODE_SERVER_EXCEPTION = 404;


	/**线上YW制版本消息服务器地址*/
	public final static String YW_DSPLUG_MESSAGE_SERVER = "www.jjyunfa.com:8080";
//	//测试
//	public final static String YW_DSPLUG_MESSAGE_SERVER = "10.10.13.252:8080";

	/**线上YW制版本服务器地址*/
	public final static String YW_DSPLUG_REGISTER_SERVER = "www.jjyunfa.com:8080";
//	//测试
//	public final static String YW_DSPLUG_REGISTER_SERVER = "10.10.13.252:8080";


	public final static String SERVER_FORMAT = "%s/%s/v1";


	/**
	 * 线上注册服务器地址
	 */
//	public final static String REGISTER_SERVER = "www.dsplug.com:8080/iadpress/v1";
	/**通讯公钥和session的url*/
	public static String URL_GET_PUBLIC_KEY = "http://%s/users/key";
	/**绑定到极简发布的url*/
	public static  String URL_BIND_DEVICE = "http://%s/authorizations/devices";
	/**用户登录的url*/
	public static  String URL_USER_LOGIN = "http://%s/users/login";
	/**用户登出的url*/
	public static  String URL_USER_LOGOUT = "http://%s/users/logout";
	/**用户信息的url*/
	public static String URL_GET_USER_INFO = "http://%s/users/userInfo";
	/**终端列表的url*/
	public static String URL_GET_DEVICES_INFO = "http://%s/devices";
	/**终端信息的url*/
	public static String URL_GET_DEVICE_INFO = "http://%s/devices/%s";
	/**设置解绑的url*/
	public static String URL_UNBIND = "http://%s/devices/controls/unbind";
	/**设置关机的url*/
	public static String URL_SHUTDOWN = "http://%s/devices/controls/shutdown";
	/**设置重启的url*/
	public static String URL_REBOOT = "http://%s/devices/controls/reboot";
	/**设置截屏的url*/
	public static String URL_SCREENSHOT = "http://%s/devices/controls/screenshot";
	/**查看最新截屏的url*/
	public static String URL_CHECK_SCREENSHOT = "http://%s/devices/%s/screenshot";
	/**
	 * 获取用户绑定的授权列表
	 */
	public static final String URL_GET_AUTHOR_LIST = "http://%s/users/%s/authorizations";

}
