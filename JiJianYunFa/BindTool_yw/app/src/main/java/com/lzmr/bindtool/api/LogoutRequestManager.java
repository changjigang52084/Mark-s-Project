package com.lzmr.bindtool.api;

import org.apache.http.Header;

import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.api.listener.BasicRequestListener;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;

public class LogoutRequestManager extends BaseHttpResponseHandler {
	private BasicRequestListener listener;
	
	public LogoutRequestManager(BasicRequestListener listener) {
		super();
		this.listener = listener;
	}
	
	/** 
	* @Title: logout 
	* @Description: 退出登录
	* @return void    返回类型
	*/ 
	public void logout(){
		String logoutUrl = HttpUtil.getLogoutServer();
		if (StringUtils.isEmpty(logoutUrl)) {
			LogUtils.e(LogUtils.getStackTraceElement(), "login url is null.");
			return;
		}
		MyHttpClient httpClient = new MyHttpClient();
		httpClient.get(logoutUrl, null, this);
	}

	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,String response, Object arg3) {
		LogUtils.d(LogUtils.getStackTraceElement(), "statusCode:" + statusCode
				+ ", response:" + response);
	}

}
