package com.lzkj.ui.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/**   
*    
* 项目名称：EPosterUI   
* 类名称：StopAppReceiver   
* 类描述：   接收关闭应用的广播
* 创建人：lyhuang   
* 创建时间：2015-9-7 下午8:09:32    
* @version    
*    
*/ 
public class StopAppReceiver  extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(StopAppReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG, "onReceive", "EPoster stop now!");
		if(PlayActivity.getActivity() != null){
			PlayActivity.getActivity().finish();
		} else {
			LogUtils.w(TAG, "onReceive", "activity is null");
		}
	}
}