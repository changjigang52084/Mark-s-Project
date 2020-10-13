package com.lzmr.bindtool.http.handler;

import org.apache.http.Header;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzmr.bindtool.http.HttpConstants;
import com.lzmr.bindtool.util.LogoutUtil;

public abstract class BaseHttpResponseHandler extends BaseJsonHttpResponseHandler{
	

	@Override
	public final void onFailure(int statusCode, Header[] headers,
			Throwable throwable, String response, Object arg4) {
		LogUtils.w(LogUtils.getStackTraceElement(),
				"statusCode:" + statusCode + ", response:" + response);
		if(HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode){
			LogoutUtil.doLogout(false);
		}
		onRequestFailure(statusCode, headers, throwable, response, arg4);
	}

	@Override
	protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}
	
	public abstract void onRequestFailure(int statusCode, Header[] headers,
			Throwable throwable, String response, Object arg4);

}
