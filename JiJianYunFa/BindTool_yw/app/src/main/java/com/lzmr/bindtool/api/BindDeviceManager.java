package com.lzmr.bindtool.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.AdpressDeviceAuthorizationDto;
import com.baize.adpress.core.protocol.dto.AdpressDeviceAuthorizationPackage;
import com.baize.adpress.core.utils.NextationCoder;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.bean.ResponseContent;
import com.lzmr.bindtool.http.HttpConstants;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;
import com.lzmr.bindtool.util.Constants;

/**
 * 绑定设备管理器
 *
 * @author lyhuang
 * @date 2016-2-18 上午11:31:58
 */
public class BindDeviceManager extends BaseHttpResponseHandler{
	private static final LogTag TAG = LogUtils.getLogTag(BindDeviceManager.class.getSimpleName(), true);
	private SessionRequestListener callback;
	public BindDeviceManager (SessionRequestListener callback){
		this.callback = callback;
	}
	
	/**
	 * 方法名或方法描述
	 * @param deviceAuthorizationPackage 设备绑定信息对象
	 * @return
	 */
	public void bind(AdpressDeviceAuthorizationPackage deviceAuthorizationPackage){
		String bindUrl = HttpUtil.getBindServer();
		if(StringUtils.isEmpty(bindUrl)){
			LogUtils.e(TAG, "bind", "Bind url is null.");
			return;
		}
		HttpEntity bindParams = getBindParams(deviceAuthorizationPackage);
		if(null == bindParams){
			LogUtils.e(TAG, "bind", "BindParams is null.");
			return;
		}
		MyHttpClient httpClient = new MyHttpClient();
		httpClient.post(bindUrl,bindParams, null,this);
	}
	
		
	
	private HttpEntity getBindParams(AdpressDeviceAuthorizationPackage deviceAuthorizationPackage) {
		if (null == deviceAuthorizationPackage) {
			LogUtils.e(TAG, "getBindParams",
					"AdpressDeviceAuthorizationPackage is null.");
			return null;
		}
		HttpEntity entity = null;
		String jsonStr = null;
//		String decryptJson = null;
		try {
			jsonStr = JSON.toJSONString(deviceAuthorizationPackage).trim();
			LogUtils.d(TAG, "getBindParams", "before encrypt："+jsonStr);
			jsonStr = NextationCoder.encryptByPublicKey(jsonStr,Constants.COMMUNICATION_PUBLIC_KEY);
			LogUtils.d(TAG, "getBindParams", "after encrypt："+jsonStr);
//			decryptJson = NextationCoder.decryptByPrivateKey(jsonStr, Constants.COMMUNICATION_PRIVATE_KEY);
//			LogUtils.d(TAG, "getBindParams", "解密后："+decryptJson);
			
			AdpressDeviceAuthorizationDto adpressDeviceAuthorizationDto = new AdpressDeviceAuthorizationDto();
			adpressDeviceAuthorizationDto.setParam(jsonStr);
//			Set<String> relationBindSet = ConfigSettings.getRelationBind();
//			if (null != relationBindSet && !relationBindSet.isEmpty() && !ConfigSettings.getBindMode()) {
//				List<Integer> bindList = new ArrayList<Integer>();
//				bindList.addAll(getBindWith(relationBindSet));
//				LogUtils.d(TAG, "getBindParams", "RelationBind："+ ConfigSettings.getRelationBind());
//				adpressDeviceAuthorizationDto.setBindWith(bindList);
//			}
			
			String dtoJson = JSON.toJSONString(adpressDeviceAuthorizationDto);
			LogUtils.d(TAG, "getBindParams", "dtoJson："+ dtoJson);
			entity = new StringEntity(dtoJson,"UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return entity;
	}
	
//	private List<Integer> getBindWith(Set<String> relationBindSet) {
//		List<Integer> bindList = new ArrayList<Integer>();
//		for (String appId : relationBindSet) {
//			bindList.add(Integer.parseInt(appId));
//		}
//		return bindList;
//	}

	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if (null != callback) {
			if (HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode) {
				callback.onSessionInvalid();
				return;
			}
			callback.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail) + errorMsg);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,	String response, Object arg3) {
		LogUtils.d(TAG, "onSuccess",
				"statusCode:" + statusCode + ", response:" + response);
		ResponseContent responseContent = null;
		try {
			responseContent = JSON.parseObject(response, ResponseContent.class);
			if(responseContent.isSuccess()){
				callback.onSuccess(StringUtils.getString(BindToolApp.getApplication(),R.string.bind_success));
			}else{
				callback.onFailure(StringUtils.getString(BindToolApp.getApplication(),R.string.bind_failure)+responseContent.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
