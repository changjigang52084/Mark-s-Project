package com.lzmr.bindtool.util;

public class Constants {
	
	/** 
	* @Fields MENU_FRAGMENT :  TODO 菜单界面
	*/ 
	public static final int MENU_FRAGMENT = 1;
	/** 
	* @Fields BIND_FRAGMENT :  TODO 绑定界面
	*/ 
	public static final int BIND_FRAGMENT = 2;
	
	/** 
	* @Fields LOGIN_FRAGMENT :  TODO 登录界面
	*/ 
	public static final int LOGIN_FRAGMENT = 3;
	
	/** 
	* @Fields DEVICE_LIST_FRAGMENT :  TODO 终端管理界面
	*/ 
	public static final int DEVICE_LIST_FRAGMENT = 4;
	
	/** 
	* @Fields SCANNING_FRAGMENT :  TODO 扫码界面
	*/ 
	public static final int SCANNING_FRAGMENT = 5;
	/**
	 * @Fields DEVICE_SETTING_FRAGMENT :  TODO 终端设置界面
	 */
	public static final int DEVICE_SETTING_FRAGMENT = 6;
	
	
	/** 
	* @Fields OPTION_SCANNING_MAC :  TODO 操作码：扫描设备二维码
	*/ 
	public static final int OPTION_SCANNING_MAC = 1;
	/** 
	* @Fields OPTION_BIND :  TODO 操作码：绑定
	*/ 
	public static final int OPTION_BIND = 2;
	
	/**
	 * AES加密秘钥
	 */
	public static final String AES_ENCRYP_KEY = "liangzhanmingrix";
	
	/**
	  * @Fields COMMUNICATION_TOKEN : TODO 位于获取公钥接口回复header的Token
	  */
	public static final String COMMUNICATION_TOKEN = "Token";
	
	/**
	 * 加密通讯公钥
	 */
	public static final String COMMUNICATION_PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJE/IV3unJ9ZXG/bFGj/BnIy/3TZrYIBgcvLlqO43lm9974tJoeiU82+rwytQbGc2a7olykQiKpgtkDzVT+wIf0CAwEAAQ==";

	/**终端列表单页数量*/
	public static final int PAGE_DEVICES_SIZE = 20;
	/**终端状态：所有状态*/
	public static final int DEVICE_STATE_ALL = 0;
	/**终端状态：工作中*/
	public static final int DEVICE_STATE_ONLINE = 1;
	/**终端状态：离线*/
	public static final int DEVICE_STATE_OFFLINE = 2;
	/**终端状态：待机*/
	public static final int DEVICE_STATE_STANDBY = 3;


	/**App类型：DSPLUG*/
	public static final int BIND_DSPLUG = 1;

	/**线上环境*/
	public static final int MODE_ONLINE = 1;
	/**灰度环境*/
	public static final int MODE_GARY = 2;
	/**线上环境*/
	public static final int MODE_OFFLINE = 3;
	
	/**yw*/
	public static final int YW = 1;

	


}
