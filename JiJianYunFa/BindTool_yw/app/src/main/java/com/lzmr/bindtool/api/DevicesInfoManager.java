package com.lzmr.bindtool.api;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.DeviceListDto;
import com.loopj.android.http.RequestParams;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.GetDevicesInfoListener;
import com.lzmr.bindtool.api.util.AuthorityCodeConstants;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.http.HttpConstants;
import com.lzmr.bindtool.http.HttpResponseUtil;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;

/**
 * 终端列表管理器
 *
 * @author longyihuang
 * @date 2016-3-11 上午11:12:18
 */
public class DevicesInfoManager extends BaseHttpResponseHandler{
	private static final LogTag TAG = LogUtils.getLogTag(DevicesInfoManager.class.getSimpleName(), true);
	private GetDevicesInfoListener callback;
	private int updateFlag;
	private int pageNumber;
	private int pageDeviceSize;
	private int state;
	/** 
	* @Title: DevicesInfoManager 
	* @Description: 获取终端列表构造器
	* @param  callback 回调接口
	* @param  updateFlag 加载标签 0：刷新， 1：加载更多
	* @param  pageNumber 页码
	* @param  pageDeviceSize 每页终端数量
	* @param  state 终端状态 0-所有 1-工作中 2-离线 3-待机
	*/ 
	public DevicesInfoManager(GetDevicesInfoListener callback, int updateFlag, int pageNumber, int pageDeviceSize,
							  int state) {
		this.callback = callback;
		this.updateFlag = updateFlag;
		this.pageNumber = pageNumber;
		this.pageDeviceSize = pageDeviceSize;
		this.state = state;
	}

	/** 
	* @Title: loadDevices 
	* @Description: 加载终端列表
	*/ 
	public void loadDevices(){
		String devicesUrl = HttpUtil.getDevicesInfoServer();
		if(StringUtils.isEmpty(devicesUrl)){
			LogUtils.e(TAG, "bind", "Devices url is null.");
			return;
		}
		RequestParams params = getDevicesParams(pageNumber, pageDeviceSize,state);
		MyHttpClient httpClient = new MyHttpClient();
//		if(!ConfigSettings.getBindMode()){
			httpClient.getClient().addHeader("Authorization-Type", AuthorityCodeConstants.AUTHORITY_CODE_DEVICE_LIST);
//		}
		httpClient.get(devicesUrl, params, this);
	}
	
	private RequestParams getDevicesParams(int pageNumber,int pageDeviceSize,int state) {
		RequestParams params = new RequestParams();
		//每页终端数量
		params.put("pnumber", pageDeviceSize);
		//页码
		params.put("ppage", pageNumber);
		//页码
		params.put("state", state);
		//是否计算套装统计信息
		params.put("statistics", true);
		//系统id
		int systemId =  Constants.BIND_DSPLUG;
		params.put("systemId", systemId);
		return params;
	}
	

	@Override
	public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable, String response, Object arg4) {
		String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
		if(null != callback){
			if(HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode){
				callback.onSessionInvalid();
				return;
			}
			callback.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail)+errorMsg);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,String response, Object arg3) {
		LogUtils.d(TAG, "onSuccess",
				"statusCode:" + statusCode + ", response:" + response);
		try {
			String responResult = HttpResponseUtil.getDataToResponseResult(response);
			if(!StringUtils.isEmpty(responResult)){
				DeviceListDto devices = JSON.parseObject(responResult,DeviceListDto.class);
				if(null != callback){
					callback.onGetDevicesInfo(updateFlag,devices);
				}
			}else{
				callback.onGetDevicesInfo(updateFlag,null);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
