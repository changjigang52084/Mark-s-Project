package com.lzmr.bindtool.http;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.util.ConfigSettings;

import android.content.Context;

public class MyHttpClient {
	private  AsyncHttpClient httpclient;
	private static final int TIME_OUT = 30 * 1000;
	public MyHttpClient(){
		httpclient = new AsyncHttpClient();
		httpclient.setTimeout(TIME_OUT);
		httpclient.addHeader("content-type", "application/json;charset=UTF-8");
		httpclient.addHeader("Token", ConfigSettings.getSession());
	}
	
	public AsyncHttpClient getClient() {
		return httpclient;
	}
	
	public  void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		LogUtils.d(LogUtils.getStackTraceElement(), "url:"+url);
		if(null != params){
			LogUtils.d(LogUtils.getStackTraceElement(),"params:"+params.toString());
		}
		httpclient.get(url, params, responseHandler);
		
	}
	
	public  void get(Context context,String url,HttpEntity entity,String contentType, AsyncHttpResponseHandler responseHandler){
		httpclient.get(context, url, entity, contentType, responseHandler);
	}

	public  void post(String url, HttpEntity entity, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		String params = null;
		try {
			params = EntityUtils.toString(entity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		LogUtils.d(LogUtils.getStackTraceElement(), "url:"+url+", params:"+params);
		httpclient.post(BindToolApp.getApplication(), url, entity, contentType, responseHandler);
	}
	
	public  void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler){
		LogUtils.d(LogUtils.getStackTraceElement(), "url:"+url);
		if(null != params){
			LogUtils.d(LogUtils.getStackTraceElement(),"params:"+params.toString());
		}
		httpclient.post(url, params, responseHandler);
	}
}
