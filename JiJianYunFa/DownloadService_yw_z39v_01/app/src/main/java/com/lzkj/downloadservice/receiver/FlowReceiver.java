package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.util.FlowManager;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.ProgramParseTools;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月10日 下午3:28:11 
 * @version 1.0 
 * @parameter 流量阀值变更广播
 */
public class FlowReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(FlowReceiver.class.getSimpleName(), true);

	private static final String ACTION = "com.lzkj.downloadservice.receiver.UPDATE_FLOW_ACTION";
	//单位M
	private static final String FLOW_KEY = "flow";
	//communication用的流量超过200M就发送一次给download 单位M
	private static final String USE_FLOW_KEY = "useFlow";
	//初始化流量
	private static final String INIT_FLOW_KEY = "initFlowKey";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			boolean isInitFlow = intent.getBooleanExtra(INIT_FLOW_KEY, false);
			if (isInitFlow) {
				FlowManager.getInstance().initFlow();
				return;
			}
			long flowValue = intent.getLongExtra(FLOW_KEY, 0);
			long userFlowValue = intent.getLongExtra(USE_FLOW_KEY, -1);
			if (flowValue == 0) {
				return;
			}
			//更新阀值 同时筛选出未下载的文件 进行下载
			if (userFlowValue != -1) {
				userFlowValue = userFlowValue * 1024 * 1024;
				FlowManager.getInstance().addDownloadFlow(userFlowValue);
			}
			flowValue = flowValue * 1024 * 1024;
			FlowManager.getInstance().setMaxFlow(flowValue);
			//筛选出可以下载的文件
			ProgramParseTools.updateWaitDownloadListToDownload();
			LogUtils.d(TAG, "onReceive", "flowValue : " + flowValue + ", userFlowValue : " + userFlowValue);
		}
	}

}
