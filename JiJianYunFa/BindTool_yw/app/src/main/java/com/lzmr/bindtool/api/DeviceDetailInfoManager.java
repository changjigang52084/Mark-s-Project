package com.lzmr.bindtool.api;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.DeviceDetailDto;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.util.AuthorityCodeConstants;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.http.HttpConstants;
import com.lzmr.bindtool.http.HttpResponseUtil;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;
import com.lzmr.bindtool.api.listener.GetDeviceDetailInfoListener;

import org.apache.http.Header;

/**
 * 终端列表管理器
 *
 * @author longyihuang
 * @date 2016-3-11 上午11:12:18
 */
public class DeviceDetailInfoManager extends BaseHttpResponseHandler{
	private static final LogTag TAG = LogUtils.getLogTag(DeviceDetailInfoManager.class.getSimpleName(), true);
	private String deviceId;
	private GetDeviceDetailInfoListener mListener;

	public DeviceDetailInfoManager(GetDeviceDetailInfoListener listener,String deviceId) {
		this.mListener = listener;
		this.deviceId = deviceId;
	}

	/** 
	* @Title: getDeviceDetailInfo
	* @Description: 获取终端信息
	*/ 
	public void getDeviceDetailInfo(){
		String deviceUrl = HttpUtil.getDeviceInfoServer(deviceId);
		if(StringUtils.isEmpty(deviceUrl)){
			LogUtils.e(LogUtils.getStackTraceElement(), "Devices url is null.");
			return;
		}
		MyHttpClient httpClient = new MyHttpClient();
//		if(!ConfigSettings.getBindMode()){
			httpClient.getClient().addHeader("Authorization-Type", AuthorityCodeConstants.AUTHORITY_CODE_DEVICE);
//		}
		httpClient.get(deviceUrl, null, this);
	}


	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if(null != mListener){
			if(HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode){
				mListener.onSessionInvalid();
				return;
			}
			mListener.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail)+errorMsg);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,String response, Object arg3) {
		LogUtils.d(TAG, "onSuccess",
				"statusCode:" + statusCode + ", response:" + response);
		try {
			String responResult = HttpResponseUtil.getDataToResponseResult(response);
			if(!StringUtils.isEmpty(responResult)){
				DeviceDetailDto deviceDetailDto = JSON.parseObject(responResult,DeviceDetailDto.class);
				if(null != mListener){
					mListener.onGetDeviceDetailInfo(deviceDetailDto);
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
