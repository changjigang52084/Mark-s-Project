package com.lzmr.bindtool.http;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;

/**
 * 
 * 项目名称：MallPoster 类名称：HttpUtil 类描述： http通讯类 创建人：lyhuang 创建时间：2015-9-14
 * 下午3:18:12
 * 
 * @version
 * 
 */
public class HttpUtil {
	private static final LogTag TAG = LogUtils.getLogTag(HttpUtil.class.getSimpleName(), true);

	public static String getResponseErrorMessage(int statusCode){
		String errorMsg = statusCode+"";
		switch (statusCode) {
		case HttpConstants.STATUS_CODE_SESSION_INVALID:
			errorMsg = StringUtils.getString(BindToolApp.getApplication(), R.string.session_invalid);
			break;
		case HttpConstants.STATUS_CODE_NETWORK_EXCEPTION:
			errorMsg = StringUtils.getString(BindToolApp.getApplication(), R.string.network_exception);
			break;
		case HttpConstants.STATUS_CODE_SERVER_EXCEPTION:
			errorMsg = StringUtils.getString(BindToolApp.getApplication(), R.string.server_exception);
			break;
		default:
			break;
		}
		return errorMsg;
	}
	
	
	/**
	 * 获取注册服务器
	 * @return
	 * 		返回注册服务器地址
	 */
	public static String getRegisterServer() {
		return String.format(HttpConstants.SERVER_FORMAT, ConfigSettings.getRegisterServerIp(),HttpConstants.DMSS_URL);
	}
	
	/**
	 * 获取消息服务器
	 * @return
	 * 		返回消息服务器地址
	 */
	public static String getMessageServer(){
		return String.format(HttpConstants.SERVER_FORMAT, ConfigSettings.getMessageServerIp(),ConfigSettings.getAppUrl());
	}
	
	
	/**
	 * 获取绑定设备的url地址
	 * @param 
	 * @return
	 */
	public static String getBindServer(){
		String registerServer = getRegisterServer();
//		String registerServer = "192.168.0.118:9090/mallappss-dmss/v1";
		return String.format(HttpConstants.URL_BIND_DEVICE, registerServer);
	}
	
	/**
	 * 获取终端收取列表信息的url地址
	 * @param 
	 * @return
	 */
	public static String getDevicesAuthServer(){
		String registerServer = getRegisterServer();
		return String.format(HttpConstants.URL_GET_AUTHOR_LIST, registerServer, ConfigSettings.getUserKey());
	}
	
	/**
	 * 获取用户登录的url地址
	 * @param 
	 * @return
	 */
	public static String getLoginServer(){
		String registerServer = getRegisterServer();
		return String.format(HttpConstants.URL_USER_LOGIN, registerServer);
	}
	
	/**
	 * 获取用户登出的url地址
	 * @param 
	 * @return
	 */
	public static String getLogoutServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_USER_LOGOUT,messageServer);
	}
	
	/**
	 * 获取用户登录的url地址
	 * @param 
	 * @return
	 */
	public static String getUserInfoServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_GET_USER_INFO,messageServer);
	}
	
	/**
	 * 获取终端列表信息的url地址
	 * @param 
	 * @return
	 */
	public static String getDevicesInfoServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_GET_DEVICES_INFO,messageServer);
	}
	
	/**
	 * 获取通讯公钥的url地址
	 * @param 
	 * @return
	 */
	public static String getPublicKeyServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_GET_PUBLIC_KEY, messageServer);
	}

	/**
	 * 获取终端信息的url地址
	 * @param deviceId 终端id
	 * @return
	 */
	public static String getDeviceInfoServer(String deviceId){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_GET_DEVICE_INFO, messageServer,deviceId);
	}

	/**
	 * 获取终端解绑的url地址
	 */
	public static String getUnbindServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_UNBIND, messageServer);
	}

	/**
	 * 获取终端关机的url地址
	 */
	public static String getShutdownServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_SHUTDOWN, messageServer);
	}

	/**
	 * 获取终端重启的url地址
	 */
	public static String getRebootServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_REBOOT, messageServer);
	}

	/**
	 * 获取终端截屏的url地址
	 */
	public static String getScreenshotServer(){
		String messageServer = getRegisterServer();
		return String.format(HttpConstants.URL_SCREENSHOT, messageServer);
	}

    /**
     * 获取查看终端最新截屏的url地址
     */
    public static String getCheckScreenshotServer(String deviceId){
        String messageServer = getRegisterServer();
        return String.format(HttpConstants.URL_CHECK_SCREENSHOT, messageServer,deviceId);
    }

}
