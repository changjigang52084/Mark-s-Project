package com.lzkj.ui.receiver;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.SharedUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月6日 下午12:09:03 
 * @version 1.0 
 * @parameter 解绑清除本地的数据
 */
public class UnbindReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(UnbindReceiver.class.getSimpleName(), true);

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedUtil.newInstance().clearAll();
		if(PlayActivity.getActivity() != null){
			PlayActivity.getActivity().finish();
		} else {
			LogUtils.w(TAG, "onReceive", "activity is null");
		}
		LogUtils.d(TAG, "onReceive", "unbind device");
	}
}
