package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzkj.launcher.MainActivity;
import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.ShareUtil;

/**   
*    
* 项目名称：Launcher   
* 类名称：UnbindReceiver   
* 类描述：接收解绑广播，拉起登录界面 
* 创建人：lyhuang   
* 创建时间：2015-9-24 下午3:23:56    
* @version    
*    
*/ 
public class UnbindReceiver extends BroadcastReceiver {

	private static final String TAG = "UnbindReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "UnbindReceiver unbind open login view.");
		ShareUtil.newInstance().setBoolean(ShareUtil.AUTHORIZE_KEY, false);
		//切换到登录界面
		if(null != MainActivity.getActivity() && 
				!MainActivity.getActivity().isFinishing()){
			MainActivity.getActivity().switchLayout(Constant.LOGIN_FRAGMENT);
		} else {
			Intent mainActivity = new Intent(context, MainActivity.class);
			mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainActivity);
		}
	}


}
