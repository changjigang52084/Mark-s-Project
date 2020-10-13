package com.lzmr.bindtool.api;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.UserInfoDto;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.BasicRequestListener;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.bean.ResponseContent;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;

/**
 * 获取用户信息管理类 
 *
 * @author longyihuang
 * @date 2016-3-8 上午10:25:21
 */
public class GetPublicKeyManager extends BaseHttpResponseHandler{
	private static final LogTag TAG = LogUtils.getLogTag(GetPublicKeyManager.class.getSimpleName(), true);
	private BasicRequestListener callback;
	public GetPublicKeyManager(BasicRequestListener callback) {
		this.callback = callback;
	}

	
	/**
	 * @Title: getUserInfo
	 * @Description: TODO(获取通讯Public key)
	 * @return void 返回类型
	 */
	public void getPublicKey() {
		String publicKeyUrl = HttpUtil.getPublicKeyServer();
		if (StringUtils.isEmpty(publicKeyUrl)) {
			LogUtils.e(LogUtils.getStackTraceElement(), "public key url is null.");
			return;
		}
		MyHttpClient httpClient = new MyHttpClient();
		httpClient.get(publicKeyUrl, null, this);
	}

	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if(null != callback){
			callback.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail)+errorMsg);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,String response, Object arg3) {
		LogUtils.d(TAG, "onSuccess", "statusCode:" + statusCode
				+ ", response:" + response);
		ResponseContent responseContent = JSON.parseObject(response, ResponseContent.class);
		if(responseContent.isSuccess()){
			String publicKey = responseContent.getData().toString();
			//保存session
			if(null != headers && headers.length>0){
				for(Header header : headers){
					if(null != header){
						if(Constants.COMMUNICATION_TOKEN.equals(header.getName())){
							String session = header.getValue();
							LogUtils.d(LogUtils.getStackTraceElement(), "session:"+session);
							ConfigSettings.saveSession(session);
							break;
						}
					}
				}
			}
			//保存public key
			if(!StringUtils.isEmpty(publicKey)){
				ConfigSettings.savePublicKey(publicKey);
				LogUtils.d(LogUtils.getStackTraceElement(), "publicKey:"+publicKey);
			}
			if(null != callback){
				callback.onSuccess("");
			}
		}else{
			if(null != callback){
				callback.onFailure(responseContent.getMessage());
			}
		}
	}
	
}
