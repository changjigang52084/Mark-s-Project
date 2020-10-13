package com.lzmr.bindtool.api;

import org.apache.http.Header;

import com.loopj.android.http.RequestParams;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.http.HttpConstants;
import com.lzmr.bindtool.http.HttpResponseUtil;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年5月13日 上午11:03:46 
 * @version 1.0 
 * @parameter  授权列表管理
 */
@SuppressWarnings("rawtypes")
public class AuthListManager extends BaseHttpResponseHandler{
	private SessionRequestListener httpRequestCallback;
	public AuthListManager(SessionRequestListener callback) {
		this.httpRequestCallback = callback;
	}
	
	/**
	 * 请求授权列表
	 */
	public void requestAuthList() {
		String authListUrl = HttpUtil.getDevicesAuthServer();
		RequestParams requestParams = new RequestParams();
		MyHttpClient httpClient = new MyHttpClient();

		httpClient.get(authListUrl, requestParams, this);
	}

	
	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if(null != httpRequestCallback){
			if(HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode){
				httpRequestCallback.onSessionInvalid();
				return;
			}
			httpRequestCallback.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail)+errorMsg);
		}
	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, String response, Object arg3) {
		if (null != httpRequestCallback) {
			try {
				String responResult = HttpResponseUtil.getDataToResponseResult(response);
				httpRequestCallback.onSuccess(responResult);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
