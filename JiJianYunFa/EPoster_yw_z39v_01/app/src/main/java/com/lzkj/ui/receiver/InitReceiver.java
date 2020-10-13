package com.lzkj.ui.receiver;

import com.lzkj.ui.util.SharedUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月27日 下午4:10:35 
 * @version 1.0 
 * @parameter  初始化本地数据
 */
public class InitReceiver extends BroadcastReceiver {
	private static final String INIT_EPOSTER_ACTION = "com.lzkj.ui.INIT_ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedUtil.newInstance().clearAll();
	}

}
