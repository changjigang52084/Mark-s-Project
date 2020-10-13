package com.lzkj.aidlservice.util;

import android.content.Intent;
import android.util.Log;

import com.baize.adpress.core.common.constant.protocol.AdpressProtocolType;
import com.lzkj.aidlservice.app.CommunicationApp;

/**
 * 命令类
 * @author changkai
 *
 */
public class Command {
	/**
	 * 通知终端立即执行待机操作，终端进行休眠操作，变成休眠状态，保持网络通信等待远程启动。
	 * 0x023003
	 */
	public static final int CMD_SHUTDOWN = AdpressProtocolType.CMD_DEVICE_SHUTDOWN_DO.getValue().intValue();
	/**
	 * 通知终端立即执行重启操作
	 * 0x023005
	 */
	public static final int CMD_REBOOT = AdpressProtocolType.CMD_DEVICE_REBOOT_DO.getValue().intValue();
	/***
	 *更新工作时间
	 *0x023006
	 */
	public static final int CMD_WORKTIME_UPDATE = AdpressProtocolType.CMD_DEVICE_WORKTIME_SETUP.getValue().intValue();
	/**
	 * 通知终端立即截图并上传到服务器
	 * 0x021008
	 */
	public static final int CMD_SNAPSHOT = AdpressProtocolType.CMD_DEVICE_SNAPSHOT_DO.getValue().intValue();
	/**
	 * 更新终端音量
	 * 0x02300D
	 */
	public static final int CMD_SCREEN_AUDIO_UPT = AdpressProtocolType.CMD_DEVICE_SCREEN_AUDIO_SETUP.getValue().intValue();
	/**
	 * 下发节目
	 * 0x02200E
	 */
	public static final int CMD_PS_UPD = AdpressProtocolType.CMD_PALYLIST_SETUP.getValue().intValue();
	/**
	 *终端解绑 
	 *0x023011
	 */
	public static final int CMD_DVC_UNBOUND = AdpressProtocolType.CMD_DEVICE_UNBIND_DO.getValue().intValue();
	
	/**撤销节目列表 
	 *0x02301A 
	 */
	public static final int CMD_CANCEL_PRMLIST = AdpressProtocolType.CMD_PALYLIST_CANCEL_SETUP.getValue().intValue();
	
	/**通知终端获取绑定信息
	 * 0x023020
	 **/
	public static final int CMD_DEVICE_ACTIVATION_SETUP = AdpressProtocolType.CMD_DEVICE_ACTIVATION_SETUP.getValue();
	/**
	  * @Fields CMD_TEST : TODO 推送测试命令
	  */
	public static final int CMD_DEVICE_TEST = AdpressProtocolType.CMD_DEVICE_TEST.getValue();
	
	/**
	 * 更新终端的流量值
	 **/
	public static final int CMD_DEVICE_LIMIT_FLOW = AdpressProtocolType.CMD_DEVICE_NETWORK_LIMITATION_SETUP.getValue();

	/**
	 * 同步终端节目
	 */
	public static final int CMD_SYNC_PLAYLIST = AdpressProtocolType.CMD_SYNC_PLAYLIST.getValue();



//	public static final int CMD_DEVICE_VENDOR_SHIPMENT_SETUP = AdpressProtocolType.CMD_DEVICE_VENDOR_SHIPMENT_SETUP.getValue();

	/**
	 * 更新终端应用<br/>
	 * 通知终端更新指定的应用程序，操作数据格式如下所示：<br/>
	"app":{
	  "id": 1,<br/>
	  "path": "xxx",<br/>
	  "name": "e-poster",<br/>
	  "pkg": "com.eposter.player",<br/>
	  "md5": "E12FA02849224DCBA6AD62849FAD2BB4",<br/>
	  "ver": "1.0.1"<br/>
	}<br/>
	[注] path表示程序存放的全路径，id表示APP id，name表示应用程序的名称，pkg表示应用程序包名，ver表示最新的版本，md5表示程序包MD5校验码。
	 */
	public static final int CMD_SYSTEM_UPDATE_APP = 0x023010;
	
	public static final int CMD_UPLOAD_LOG = 0x023015;
	

	/**
	 * 发送各种命令给系统
	 * @param cmd 命令值 {@link Constant}
	 */
	public static void sendCmdAction(int cmd, Intent cmdIntent) {
		if (Util.isInstallApp(CommunicationApp.get(), Constant.CTRL_PKG)) {
			if (null == cmdIntent) {
				cmdIntent = new Intent();
			}
			cmdIntent.setAction(Constant.CTRL_ACTION);
			cmdIntent.putExtra(Constant.CMD, cmd);
			cmdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			CommunicationApp.get().startService(cmdIntent);
			Log.d("sendCmdAction", "cmd:" + cmd);
		} else if (Constant.REBOOT == cmd){
			Util.reboot();
		} 
	}
}
