package com.lzmr.bindtool.api;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.utils.NextationCoder;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.BasicRequestListener;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.bean.ResponseContent;
import com.lzmr.bindtool.bean.UserParams;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;
import com.lzmr.bindtool.util.ConfigSettings;
/**
 * 用户登录管理器
 * 
 * @author longyihuang
 * @date 2016-3-7 下午5:31:00
 */
public class LoginRequestManager extends BaseHttpResponseHandler{

	private static final LogTag TAG = LogUtils.getLogTag(
			LoginRequestManager.class.getSimpleName(), true);
	private String userName;
	private String password;
	private SessionRequestListener listener;
	
	/**
	 * @Title: LoginRequestManager
	 * @Description: TODO 登录管理器构造方法
	 * @param userName 用户名
	 * @param password 密码
	 * @return void 返回类型
	 */
	public LoginRequestManager(String userName, String password, SessionRequestListener listener) {
		this.userName = userName;
		this.password = password;
		this.listener = listener;
	}
	
	public void getPublicKeyForLogin(){
		GetPublicKeyManager getPublicKeyManager = new GetPublicKeyManager(
				new GetPublicKeyListener());
		getPublicKeyManager.getPublicKey();
	}

	/**
	 * @Title: login
	 * @Description: TODO(用户登录)
	 * @return void 返回类型
	 */
	private void login() {
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
			LogUtils.e(TAG, "login", "User name or passwork is null.");
			return;
		}
		String loginUrl = HttpUtil.getLoginServer();
		if (StringUtils.isEmpty(loginUrl)) {
			LogUtils.e(TAG, "login", "login url is null.");
			return;
		}
		String publicKey = ConfigSettings.getPublicKey();
		if(StringUtils.isEmpty(publicKey)){
			LogUtils.e(TAG, "login", "communicaion public key is null.");
			return;
		}
		try {
			password = NextationCoder.encryptByPublicKey(password, publicKey);
//			password = AesCoderUtils.aesEncrypt(password, Constants.AES_ENCRYP_KEY);
//			password = "E/Zyyrf0QHz1+tJ0AAWdDQ==";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpEntity params = getLoginParams(userName, password);
		if(null == params){
			LogUtils.e(TAG, "login", "params is null.");
			return;
		}
		
		MyHttpClient httpClient = new MyHttpClient();
		httpClient.post(loginUrl, params,null, this);
	}
	
	private class GetPublicKeyListener implements BasicRequestListener{
		@Override
		public void onSuccess(String msg) {
			login();
		}

		@Override
		public void onFailure(String msg) {
			if(null != listener){
				listener.onFailure(msg);
			}
			
		}
		
	}
	
	private HttpEntity getLoginParams(String account,String password) {
		UserParams params = new UserParams();
		params.setAccount(account);
		params.setPassword(password);
		HttpEntity paramsStr = null;
		try {
			paramsStr = new StringEntity(JSON.toJSONString(params));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paramsStr;
	}

	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if(null != listener){
			listener.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail)+errorMsg);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,String response, Object arg3) {
		LogUtils.d(TAG, "onSuccess", "statusCode:" + statusCode
				+ ", response:" + response);
		ResponseContent responseContent = JSON.parseObject(response, ResponseContent.class);
		if(null != listener){
			if(responseContent.isSuccess()){
				UserInfoManager userInfoManager = new UserInfoManager(new SessionRequestListener() {

					@Override
					public void onSessionInvalid() {
						listener.onSessionInvalid();
					}

					@Override
					public void onSuccess(String msg) {
						listener.onSuccess(StringUtils.getString(BindToolApp.getApplication(),R.string.login_success));
					}

					@Override
					public void onFailure(String msg) {
						listener.onFailure(StringUtils.getString(BindToolApp.getApplication(),R.string.login_failure)+msg);
					}

				});
				userInfoManager.getUserInfo();
			}else{
				listener.onFailure(StringUtils.getString(BindToolApp.getApplication(),R.string.login_failure)+responseContent.getMessage());
			}
		}
	}


}
