package com.lzmr.bindtool.api;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.UserInfoDto;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
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
import com.lzmr.bindtool.util.ConfigSettings;

/**
 * 获取用户信息管理类 
 *
 * @author longyihuang
 * @date 2016-3-8 上午10:25:21
 */
public class UserInfoManager extends BaseHttpResponseHandler{
	private static final LogTag TAG = LogUtils.getLogTag(UserInfoManager.class.getSimpleName(), true);
	private SessionRequestListener listener;
	public UserInfoManager(SessionRequestListener listener) {
		this.listener = listener;
	}
	
	/**
	 * @Title: getUserInfo
	 * @Description: TODO(获取用户信息)
	 * @return void 返回类型
	 */
	public void getUserInfo() {
		String userInfoUrl = HttpUtil.getUserInfoServer();
		if (StringUtils.isEmpty(userInfoUrl)) {
			LogUtils.e(TAG, "login", "User info url is null.");
			return;
		}
		MyHttpClient httpClient = new MyHttpClient();
		httpClient.get(userInfoUrl, null, this);
	}

	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if(null != listener){
			if(HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode){
				listener.onSessionInvalid();
				return;
			}
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
				String data = responseContent.getData().toString();
				UserInfoDto userInfo = JSON.parseObject(data, UserInfoDto.class);
				if(null != userInfo){
					LogUtils.d(TAG, "onSuccess", "userkey:"+userInfo.getUserKey());
					ConfigSettings.saveUserKey(userInfo.getUserKey());
					listener.onSuccess("");
				}
			}else{
				listener.onFailure(responseContent.getMessage());
			}
		}

	}
	
}
