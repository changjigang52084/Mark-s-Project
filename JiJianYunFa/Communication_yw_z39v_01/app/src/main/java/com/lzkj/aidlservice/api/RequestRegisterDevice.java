package com.lzkj.aidlservice.api;

import com.alibaba.fastjson.JSON;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HeratServerResponse;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Decryptor;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月16日 上午10:45:57 
 * @version 1.0 
 * @parameter  请求注册终端和恢复注册
 */
public class RequestRegisterDevice implements IRequestCallback {
	private static final LogTag LOG_TAG = LogUtils.getLogTag(RequestRegisterDevice.class.getSimpleName(), true);
	
	private static final int SUCCESS_CODE = 0;
	
	/**
	 * 恢复注册
	 * @param type
	 * @param macAddress
	 */
	public void recoveryRegister(int type, String macAddress) {
		HttpRequestBean httpRequestBean = new HttpRequestBean();
		httpRequestBean.setRequestUrl(HttpConfigSetting.getRecoveryRegisterUrl());
		httpRequestBean.setRequestCallback(RequestRegisterDevice.this);
		httpRequestBean.setRequestParm(getRecoveryData(type,macAddress));
		httpRequestBean.setRequestTag("LoginClientAIDL");
		HttpUtil.newInstance().sendGetRequest(httpRequestBean);
	}
	
	/**
	 * 获取要发送恢复注册的数据
	 * @param type
	 * 			消息的类型
	 * @param macAddress
	 * 			mac地址
	 * @return
	 */	
	private String getRecoveryData(int type, String macAddress) {
		String cid = SharedUtil.newInstance().getString(SharedUtil.CLIENT_ID);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("dcd=");
		stringBuffer.append(macAddress);
		stringBuffer.append("&type=");
		stringBuffer.append(type);
		stringBuffer.append("&cid=");
		stringBuffer.append(cid);
		return stringBuffer.toString();
	}
	   /**
		 * 获取要发送注册请求的数据
		 * @param type
		 * @param deviceName
		 * @param userName
		 * @param password
		 * @param macAddress
		 * @return
		 */
	private String getRegisterData(int type, String deviceName,String userName,String password,String macAddress) {
		//发送注册请求
		StringBuffer jsonData = new StringBuffer();
		jsonData.append("type=");
		jsonData.append(type);
		jsonData.append("&name=");
		jsonData.append(deviceName);
		jsonData.append("&dcd=");
		jsonData.append(macAddress);
		jsonData.append("&userid=");
		jsonData.append(userName);
		jsonData.append("&passwd=");
		jsonData.append(password);
		jsonData.append("&bussid=");
		jsonData.append("");
		jsonData.append("&resolution_attr=");//测试屏幕分辨率
		jsonData.append(ConfigSettings.SCREEN_WIDTH+"*"+ConfigSettings.SCREEN_HEIGHT);
		LogUtils.d(LOG_TAG, "onLogin"," type:"+type+", deviceName:"+deviceName+
										",userName:"+userName+",password"+password+",macAddress:"+macAddress);
		return jsonData.toString();
	}

	@Override
	public void onSuccess(String result, String tag, String requestUrl) {
		try {
			//在这里判断是否注册成功，成功了以后就打开推送
			LogUtils.d(LOG_TAG, "onSuccess","result"+result);
			HeratServerResponse registrResponse = JSON.parseObject(result, HeratServerResponse.class);
			checkRspCode(registrResponse, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据code来判断是否注册是否成功
	 * @param registrResponse
	 * @param result
	 * @throws Exception 
	 */
	private void checkRspCode(HeratServerResponse registrResponse,String result) throws Exception {
		if (registrResponse.getRsp() == null) {
			return;
		}
		int code = registrResponse.getRsp().getCode();
		int type = registrResponse.getType();//根据类型判断是恢复注册请求还是注册请求
		if (code == SUCCESS_CODE) {
			String lic = registrResponse.getRsp().getLicense();
			SharedUtil.newInstance().setString(SharedUtil.LICENSE_ID, lic);
			getHeartServerToLidAndDid(lic);
			//同步设备
//			CommunicationApp.get().syncDeviceInfo();
		} else {
			onFaile(registrResponse.getRsp().getNote(), "", "");
		}
			
	}
	/**
	 * 根据lid,did获取心跳服务器
	 * @throws Exception 
	 */
	private void getHeartServerToLidAndDid(String lic) throws Exception {
		if (null == lic) {
			return; 
		}
		String license = Decryptor.deCipherMsg(lic);
		String data[] = license.split(";");
		if(data.length < 2){
			LogUtils.e(LOG_TAG, "syncServer", "error license");
			return;
		}
	}
	
	@Override
	public void onFaile(String errMsg, String tag, String requestUrl) {
		LogUtils.d(LOG_TAG, "onFaile","result"+errMsg);
	}
}
