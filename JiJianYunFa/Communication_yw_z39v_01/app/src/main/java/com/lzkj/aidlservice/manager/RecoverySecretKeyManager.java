package com.lzkj.aidlservice.manager;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.AdpressDeviceValidatePackage;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.bo.ResponseContent;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.StringUtil;

/**
 * 终端丢失通讯秘钥管理器
 *
 * @author longyihuang
 * @date 2016-3-15 下午7:11:04
 */
public class RecoverySecretKeyManager implements IRequestCallback {
    private static final LogTag TAG = LogUtils.getLogTag(RecoverySecretKeyManager.class.getSimpleName(), true);
//	private static LoseCommunicationKeyManager instance;

    private IRequestCallback mIRequestCallback;

//	private LoseCommunicationKeyManager() {
//	}
//
//	public static LoseCommunicationKeyManager getInstance() {
//		if (null == instance) {
//			instance = new LoseCommunicationKeyManager();
//		}
//		return instance;
//	}

    /**
     * @return void    返回类型
     * @Title: getCommunicationKeyFromServer
     * @Description: 向服务器领取通讯秘钥
     */
    public void getSecretKey(String macAddress, IRequestCallback callback) {
        if (!StringUtil.isNullStr(macAddress)) {
            mIRequestCallback = callback;
            HttpRequestBean httpRequestBean = new HttpRequestBean();
            httpRequestBean.setRequestUrl(HttpConfigSetting.getLoseCommunicaionUrl());
            httpRequestBean.setRequestRestry(5);
            httpRequestBean.setRequestCallback(this);
            httpRequestBean.setRequestParm(getValidationData(macAddress));
            httpRequestBean.setRequestTag(this.getClass().getSimpleName());
            HttpUtil.newInstance().postRequest(httpRequestBean);
        }
    }

//	/** 
//	 * @Title: getMallCommunicationKeyFromServer 
//	 * @Description: 向服务器领取通讯秘钥
//	 * @param     设定文件 
//	 * @return void    返回类型 
//	 */ 
//	public void getMallCommunicationKeyFromServer(String macAddress, IRequestCallback callback){
//		if (!StringUtil.isNullStr(macAddress)) {
//			mIRequestCallback = callback;
//			HttpRequestBean httpRequestBean = new HttpRequestBean();
//			httpRequestBean.setRequestUrl(ConfigSettings.getMallCommunicaionUrl());
//			httpRequestBean.setRequestRestry(5);
//			httpRequestBean.setRequestCallback(this);
//			httpRequestBean.setRequestParm(getValidationData(macAddress));
//			httpRequestBean.setRequestTag(this.getClass().getSimpleName());
//			HttpUtil.newInstance().postRequest(httpRequestBean);	
//		}
//	}

    /**
     * 获取要发送验证绑定的数据
     *
     * @param macAddress mac地址
     * @return
     */
    private String getValidationData(String macAddress) {
//		AdpressDeviceValidatePackageSub  deviceValidatePackage= new AdpressDeviceValidatePackageSub();
//	 	deviceValidatePackage.setMac(macAddress);
//	 	deviceValidatePackage.setGetuiId(ConfigSettings.getClientId());
        AdpressDeviceValidatePackage deviceValidatePackage = new AdpressDeviceValidatePackage();
        deviceValidatePackage.setMac(macAddress);
        return JSON.toJSONString(deviceValidatePackage);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        LogUtils.d(TAG, "onSuccess", "result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        //SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
        ResponseContent responseContent = JSON.parseObject(result, ResponseContent.class);
        if (null != responseContent && !responseContent.isSuccess()) {
            if (null != mIRequestCallback) {
                mIRequestCallback.onFaile(responseContent.getMessage(), httpTag, requestUrl);
            }
        }
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        LogUtils.d(TAG, "onFaile", "errMsg: " + errMsg);
        if (null != mIRequestCallback) {
            mIRequestCallback.onFaile(errMsg, httpTag, requestUrl);
        }
    }
}
