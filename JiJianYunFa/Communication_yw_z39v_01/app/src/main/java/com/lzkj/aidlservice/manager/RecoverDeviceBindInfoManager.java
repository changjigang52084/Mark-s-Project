package com.lzkj.aidlservice.manager;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandDeviceActivationSetup;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

/**
 * 推送恢复终端绑定信息
 *
 * @author lyhuang
 * @date 2016-2-19 下午6:31:24
 */
public class RecoverDeviceBindInfoManager implements IRequestCallback {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(RecoverDeviceBindInfoManager.class.getSimpleName(), true);
	
	private RecoverDeviceBindInfoManager(){
	}
	
	public static RecoverDeviceBindInfoManager get(){
		return RecoverDeviceBindInfoInstance.INSTANCE;
	}
	
	private static class RecoverDeviceBindInfoInstance {
		private static final RecoverDeviceBindInfoManager INSTANCE = new RecoverDeviceBindInfoManager();
	}
	
	/**
	 * 恢复终端绑定信息
	 * @param 
	 * @return
	 */
	public void recoverDeviceBindInfo(AdpressDataPackage adpressDataPackage){
		SharedUtil.newInstance().setString(SharedUtil.VALIDATION_DEVICE_ADPRESSDATAPACKAGE, 
				adpressDataPackage.toString());
		
		CommandDeviceActivationSetup  activationSetup = null;
		try {
			activationSetup = adpressDataPackage.getData();
			LogUtils.e(TAG_LOG, "recoverDeviceBindInfo", JSON.toJSONString(activationSetup));
			if (null != activationSetup) {
				saveRegisterServerAndKey(activationSetup);
				ValidationDeviceManager.get().validationDevice(ConfigSettings.MAC_ADDRESS, this);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	  * @Title: saveRegisterServerAndKey
	  * @Description: TODO 保存注册服务器和秘钥
	  */
	private void saveRegisterServerAndKey(CommandDeviceActivationSetup activationSetup) {
		//保存注册服务器
		HttpConfigSetting.saveRegisterServer(activationSetup.getServer());
		//保存通讯秘钥
		ConfigSettings.saveCommunicationKey(activationSetup.getPublicKey());
	}

	@Override
	public void onSuccess(String result, String httpTag, String requestUrl) {
		LogUtils.d(TAG_LOG, "onSuccess", "result: " + result);
		//启动所有服务
		ServiceManager.getInstance().startAllService();
		sendCommandReceipt(CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS);
		notifyUpdateAuthorization(true, result);
	}

	@Override
	public void onFaile(String errMsg, String httpTag, String requestUrl) {
		LogUtils.d(TAG_LOG, "onFaile", "httpTag: " + httpTag);
		sendCommandReceipt(CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR);
		notifyUpdateAuthorization(false, errMsg);
	}
	
	/**
	 * 发送广播通知更新授权信息
	 * @param 
	 * @return
	 */
	private void notifyUpdateAuthorization(boolean authorization, String message){
		Intent updateAuthorizationIntent = new Intent(Constant.LAUNCHER_UPDATE_AUTHORIZATION_ACTION);
		updateAuthorizationIntent.putExtra(Constant.AUTHORIZE_KEY, authorization);
		updateAuthorizationIntent.putExtra(Constant.AUTHORIZE_MESSAGE, message);
		CommunicationApp.get().sendBroadcast(updateAuthorizationIntent);
	}
	
	private void sendCommandReceipt(int status){
		try {
			String adpressData = SharedUtil.newInstance().getString(SharedUtil.VALIDATION_DEVICE_ADPRESSDATAPACKAGE);
			if (StringUtil.isNullStr(adpressData)) {
				return;
			}
			SharedUtil.newInstance().removeKey(SharedUtil.VALIDATION_DEVICE_ADPRESSDATAPACKAGE);
			AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(adpressData);
			//发送回执
			CommandReceiptManager.commandReceipt(adpressDataPackage, status, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
