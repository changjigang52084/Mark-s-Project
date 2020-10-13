package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.api.sync.RequestSyncUpdateProgram;
import com.lzkj.aidlservice.util.ThreadPoolManager;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月13日 下午4:37:06 
 * @version 1.0 
 * @parameter 请求同步节目的广播
 */
public class RequestSyncPrmReceiver extends BroadcastReceiver {

	/**RequestSyncPrmReceiver广播拦截的action*/
	private static final String REQUEST_SYNC_PRM_RECEIVER_ACTION = "com.lzkj.aidlservice.receiver.REQUEST_SYNC_PRM_ACTION";
	@Override
	public void onReceive(Context context, Intent intent) {
		ThreadPoolManager.get().addRunnable(new RequestSyncUpdateProgram(null));
	}
}
