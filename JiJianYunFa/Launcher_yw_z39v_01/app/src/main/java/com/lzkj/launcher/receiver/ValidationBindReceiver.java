package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.service.LoginCallbackManager;
import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.ShareUtil;

/**
 * 接收终端验证授权的广播
 *
 * @author lyhuang
 * @date 2016-2-19 下午6:25:25
 */
public class ValidationBindReceiver extends BroadcastReceiver {

	private static final String TAG = "ValidationBind";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "ValidationBind onReceive ValidationBind.");
		if(intent == null){
			return;
		}
		if(Constant.LAUNCHER_UPDATE_AUTHORIZATION_ACTION.equals(intent.getAction())){
			boolean isBind = intent.getBooleanExtra(ShareUtil.AUTHORIZE_KEY, false);
			String message = intent.getStringExtra(Constant.AUTHORIZE_MESSAGE);
			Log.d(TAG, "ValidationBind onReceive isBind: "+ isBind + " ,message: "+ message);
			if(isBind){
				LoginCallbackManager.newInstance().setSuccess(message);
			} else {
				LoginCallbackManager.newInstance().setFail(message);
			}
		}
	}

	/***
	 * 启动epost
	 * @param context
	 */
	private void startEpost(Context context) {
		Intent epostIntent = new Intent();
		ComponentName component = new ComponentName(Constant.UI_PKG, Constant.UI_ACT);
		epostIntent.setComponent(component);
		epostIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(epostIntent);
	}
}
