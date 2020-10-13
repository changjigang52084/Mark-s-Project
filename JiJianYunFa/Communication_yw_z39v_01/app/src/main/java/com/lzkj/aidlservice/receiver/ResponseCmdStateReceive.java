package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.manager.ReportDeviceStatusManager;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年7月7日 上午10:06:58 
 * @version 1.0 
 * @parameter  接收命令执行的状态
 */
public class ResponseCmdStateReceive extends BroadcastReceiver {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(ResponseCmdStateReceive.class.getSimpleName(), true);
	/**命令执行成功*/
	private final static int SUCCESS_CODE = 1;
	/**命令执行失败*/
	private final static int FAIL_CODE = -1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG_LOG, "onReceive", "ResponseCmdStateReceive");
		if (null == intent) {
			return;
		}
		int cmd = intent.getIntExtra(Constant.CMD, -1);
		excuteCmd(cmd, intent, context);
	}

	/**
	 * 执行对应的命令
	 * @param cmd
	 * 			命令值
	 * @param intent
	 * 			包含控制命令和参数的intent
	 * @param cxt
	 * 			Context对象
	 */
	private void excuteCmd(int cmd, Intent intent, Context cxt) {
		switch (cmd) {
			case Constant.REBOOT:
				 reboot(intent);
				break;
			case Constant.SHUTDOWN:
				 shutdown(intent);
				break;
			case Constant.SWITCH_WORK_TIME:
				setWorkTime(intent);
				break;
			case Constant.SILENCE_INSTALL:
				installApp(intent);
				break;
			case Constant.SILENCE_UNINSTALL:
//				uninstallApp(intent);
				break;
			default:
				break;
		}
	}
	/**
	 * 判断系统是否执行了重启命令
	 * @param intent
	 * 			包含返回的参数
	 */
	private void reboot(Intent intent) {
		reportCmdState(intent, SharedUtil.REBOOT_ADPRESSDATAPACKAGE);
		ReportDeviceStatusManager
		.get()
		.reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_STARTUP);
	}
	/**
	 * 判断系统是否执行了关机命令
	 * @param intent
	 * 			包含返回的参数
	 */
	private void shutdown(Intent intent) {
		reportCmdState(intent,SharedUtil.SHUTDOWN_ADPRESSDATAPACKAGE);
		ReportDeviceStatusManager.get()
		.reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_SHUTDOWN);
	}
	/**
	 * 判断系统是否执行了定时开关机命令
	 * @param intent
	 * 			包含返回的参数
	 */
	private void setWorkTime(Intent intent) {
		reportCmdState(intent, SharedUtil.WORKTIME_ADPRESSDATAPACKAGE);
	}
	
	/**
	 * 判断系统是否执行了安装命令
	 * @param intent
	 * 			包含返回的参数
	 */
	private void installApp(Intent intent) {
		int installState = intent.getIntExtra("state", 0);
		if (installState == SUCCESS_CODE) {
			String appName = intent.getStringExtra("extraData");
			if (appName.contains("EPoster")) {
				startEposter();
			}
		}
	}
	
	/**
	 * 启动eposter
	 */
	private void startEposter() {
		Intent eposterIntent = new Intent();
		ComponentName component = new ComponentName(Constant.UI_PKG, Constant.UI_ACT);
		eposterIntent.setComponent(component);
		eposterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		CommunicationApp.get().startActivity(eposterIntent);
	}

	/**
	 * 汇报服务器命令执行成功或者失败
	 * @param intent 包含执行结果的intent
	 * @param shredKey 命令对应的AdpressDataPackage保存key
	 */
	private void reportCmdState(Intent intent, String shredKey) {
		int resultCode = intent.getIntExtra("state", -1);
		switch (resultCode) { 
			case SUCCESS_CODE:
				report(shredKey, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS);
				break;  
			case FAIL_CODE:
				report(shredKey, CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 汇报命令执行成功
	 * @param shredKey 保存在shred里面的AdpressDataPackage对象
	 */
	private void report(String shredKey, Integer responseCode) {
		try {
			String adpressData = SharedUtil.newInstance().getString(shredKey);
			if (StringUtil.isNullStr(adpressData)) {
				return;
			}
			SharedUtil.newInstance().removeKey(shredKey);
			AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(adpressData);
			responseReceiver(adpressDataPackage, responseCode, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 命令接收成功返回给服务器
	 * @param adpressDataPackage
	 * @param responseCode
	 */
	private void responseReceiver(AdpressDataPackage adpressDataPackage, int responseCode, String jsonData) {
		CommandReceiptManager.commandReceipt(adpressDataPackage, responseCode, jsonData);
	}
}
