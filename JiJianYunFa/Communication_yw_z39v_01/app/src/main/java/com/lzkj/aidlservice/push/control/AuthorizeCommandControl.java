package com.lzkj.aidlservice.push.control;

import android.widget.Toast;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.manager.RecoverDeviceBindInfoManager;
import com.lzkj.aidlservice.manager.UnbindDeviceManager;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月14日 下午7:44:32 
 * @version 1.0 
 * @parameter 授权相关的控制命令类
 */
public class AuthorizeCommandControl{
	private static final LogTag TAG_LOG = LogUtils.getLogTag(AuthorizeCommandControl.class.getSimpleName(), true);
	
	/**
	 * 更新终端授权信息
	 * @param adpressDataPackage
	 */
	void updateDeviceAuthorizeInfo(AdpressDataPackage adpressDataPackage) {
		RecoverDeviceBindInfoManager.get().recoverDeviceBindInfo(adpressDataPackage);
	}
	
	/**
	 * 解绑终端
	 */
	void unbindDevice(AdpressDataPackage adpressDataPackage) {
		LogUtils.i(TAG_LOG, "unbindDevice", "do unbind device");
		//第一将展示界面切换到登录界面，将保存在本地的素材删除，停止心跳，停止推送，删除保存在本地的clientid,删除应用中保存在shared里面的所有数据
		UnbindDeviceManager.newInstance().unbindDevice();
		Toast.makeText(CommunicationApp.get(), R.string.response_unbind, Toast.LENGTH_LONG).show();
		responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
	}
	/**
	 * 命令接收成功返回给服务器
	 * @param adpressDataPackage AdpressDataPackage对象
	 * @param responseCode 响应码
	 * @param jsonData 说明内容
	 */
	private void responseReceiver(AdpressDataPackage adpressDataPackage, int responseCode, String jsonData) {
		CommandReceiptManager.commandReceipt(adpressDataPackage, responseCode, jsonData);
	}
}
