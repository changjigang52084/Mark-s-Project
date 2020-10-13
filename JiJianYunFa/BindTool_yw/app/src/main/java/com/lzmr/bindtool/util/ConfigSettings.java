package com.lzmr.bindtool.util;

import java.util.Set;

import android.os.Environment;

import com.baize.adpress.core.common.constant.SystemConstant;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.http.HttpConstants;

import static com.alibaba.fastjson.asm.Opcodes.RETURN;


/**
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年8月3日 下午3:07:27 
 * @version 1.0 
 * @parameter  配置类
 */
public class ConfigSettings {
	
	
	/**设置Log是否开启,true表示开启*/
	public static final boolean LOG_TAG = true;
	
	/**设置是否显示可选择 内网，外网*/
	public static boolean IS_SHOW_SELET_DEBUG = false;
	
	/**设置是否为social,true表示social，false表示 dsplug*/
	public static final boolean IS_SOCIAL = false;
	
	public static final int CLIENT_TYPE = Constants.YW;

	/** 存储路径 */
	public static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public static boolean checkLogin(){
		String session = getSession();
		String publicKey = getPublicKey();
		String userKey = getUserKey();
		if(!StringUtils.isEmpty(session) && !StringUtils.isEmpty(publicKey)
				&& !StringUtils.isEmpty(userKey)){
			return true;
		}
		return false;
	}
	
	/**
	  * @Title: savePublicKey
	  * @Description: TODO 保存通讯公钥
	  * @param  publicKey  通讯公钥
	  */
	public static void savePublicKey(String publicKey){
		ShareUtil.newInstance().setString(ShareUtil.PUBLIC_KEY, publicKey);
	}
	
	
	/**
	  * @Title: getPublicKey
	  * @Description: TODO 获取通讯公钥
	  * @return String  通讯公钥
	  * @throws
	  */
	public static String getPublicKey(){
		return ShareUtil.newInstance().getString(ShareUtil.PUBLIC_KEY);
	}
	
	/**
	  * @Title: saveSession
	  * @Description: TODO 保存通讯Session
	  * @param  session  通讯Session
	  */
	public static void saveSession(String session){
		ShareUtil.newInstance().setString(ShareUtil.SESSION, session);
	}
	
	/**
	  * @Title: getSession
	  * @Description: TODO 获取通讯Session
	  * @return String  通讯Session
	  * @throws
	  */
	public static String getSession(){
		return ShareUtil.newInstance().getString(ShareUtil.SESSION);
	}
	
	
	/** 
	* @Title: saveUserKey 
	* @Description: 保存userkey
	* @param userKey  
	* @return void 
	*/ 
	public static void saveUserKey(String userKey){
		ShareUtil.newInstance().setString(ShareUtil.USER_KEY, userKey);
	}
	
	/** 
	* @Title: getUserKey 
	* @Description: 获取userkey
	* @return String  userkey
	*/ 
	public static String getUserKey(){
		return ShareUtil.newInstance().getString(ShareUtil.USER_KEY);
	}
	
	/** 
	* @Title: setTestMode 
	* @Description: 设置测试模式
	* @return void
	*/ 
	public static void setTestMode(int testFlag){
		ShareUtil.newInstance().setInt(ShareUtil.LOGIN_MODE, testFlag);
	}
	
	/** 
	* @Title: getTestMode 
	* @Description: 获取测试模式
	* @return boolean    返回类型 
	*/ 
	public static int getTestMode(){
		int defaultMode = Constants.MODE_ONLINE;
		int mode = ShareUtil.newInstance().getInt(ShareUtil.LOGIN_MODE);
		if(mode > 0){
			LogUtils.e(LogUtils.getStackTraceElement(), "MODE:" + mode);
			return mode;
		}
		return defaultMode;
	} 
	
	/** 
	* @Title: setBindMode 
	* @Description: 设置绑定模式
	* @param isBindToSocial  
	*/ 
	public static void setBindMode(boolean isBindToSocial){
		ShareUtil.newInstance().setBoolean(ShareUtil.BIND_MODE, isBindToSocial);
	}
	
	/** 
	* @Title: getBindMode 
	* @Description: 获取绑定模式
	* @return boolean   true-Dsplug-Social  false-Dsplug
	*/ 
	public static boolean getBindMode(){
		return ShareUtil.newInstance().getBoolean(ShareUtil.BIND_MODE);
	} 
	/** 
	 * @Description: 获取是否关联绑定模式
	 * @return boolean   true-Dsplug-Social  false-Dsplug
	 */ 
	public static Set<String> getRelationBind(){
		return ShareUtil.newInstance().getStringSet(ShareUtil.RELATION_BIND);
	} 
	
	/**
	 * 设置关联那个app
	 * @param relationAppId app id
	 */
	public static void setRelationBindSet(Set<String> relationAppId){
		ShareUtil.newInstance().setStringSet(ShareUtil.RELATION_BIND, relationAppId);
	} 
	
	/**
	 * 根据当前配置的app 获取对应消息服务器地址
	 */
	public static String getMessageServerIp() {
		return getDsplugMessageServer();
	}

	
	/**
	 * 根据客户的类型返回不同的服务器地址
	 * @return
	 */
	private static String getDsplugMessageServer() {
		return HttpConstants.YW_DSPLUG_MESSAGE_SERVER;
	}
	
	/**
	  * 根据当前配置的app 获取对应注册服务器地址
	  */
	public static String getRegisterServerIp(){
		return HttpConstants.YW_DSPLUG_REGISTER_SERVER;
	}

	
	/**
	 * 根据当前配置的app 获取对应的系统项目Url
	 */
	public static String getAppUrl() {
		return HttpConstants.DSPLUG_URL;
	}
	
	/**
	  * @Title: getSystemType
	  * @Description: TODO 获取系统类型，根据用户登录界面的系统选项而定
	  * @return Integer DSPLUG类型1   SOCIAL类型：2
	  */
	public static Integer getSystemType(){
		return SystemConstant.SYSTEM_TYPE_DSPLUG;
	}
	
}
