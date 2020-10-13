package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

/**   
*    
* 项目名称：Communication   
* 类名称：InstallAppReceive   
* 类描述： 接收安装应用广播，汇报调度命令正在进行中的回执
* 创建人：  lyhuang
* 创建时间：2015-8-26 下午5:54:47    
* @version    
*    
*/ 
public class InstallAppReceive extends BroadcastReceiver {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(InstallAppReceive.class.getSimpleName(), true);

	@Override
	public void onReceive(Context context, Intent intent) {
		//发送汇报调度命令正在进行中的回执
//		report(SharedUtil.DISPATCH_SETUP_ADPRESSDATAPACKAGE,CommandStateConstant.COMMAND_STATE_EXECUTING);
	}
	
	/**
	 * 汇报命令正在进行中的回执
	 * @param shredKey
	 * 			保存在shred里面的AdpressDataPackage对象
	 */
	private void report(String shredKey, Integer responseCode) {
		try {
			LogUtils.d(TAG_LOG, "report", "report COMMAND_STATE_EXECUTING");
			String adpressData = SharedUtil.newInstance().getString(shredKey);
			if (StringUtil.isNullStr(adpressData)) {
				return;
			}
			AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(adpressData);
			CommandReceiptManager.commandReceipt(adpressDataPackage, responseCode, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
