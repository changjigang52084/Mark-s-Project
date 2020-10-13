package com.lzmr.bindtool.app;


import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.util.ConfigSettings;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;

public class BindToolApp extends Application {
	private static final LogTag TAG = LogUtils.getLogTag(BindToolApp.class.getSimpleName(), true);
	private static BindToolApp bindToolApp;
	public Handler mHandler = new Handler();
	public static BindToolApp getApplication() {
		return bindToolApp;
	}
	private AlertDialog dialog;
	@Override
	public void onCreate() {
		super.onCreate();
		bindToolApp = this;
		//设置控制台log
		LogUtils.setLogTag(ConfigSettings.LOG_TAG);

	}

	/***************** 提示 ************************************************/
	public synchronized void showDialog(Context context, String message, DialogInterface.OnClickListener listener){
		if (null!=dialog && dialog.isShowing()) {
			LogUtils.i(LogUtils.getStackTraceElement(),"重复打开对话框");
			dialog.dismiss();
			dialog = null;
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		if(null == listener){
			builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
		}else{
			builder.setPositiveButton(this.getString(R.string.yes),listener);
			builder.setNegativeButton(this.getString(R.string.no),null);
		}
		dialog = builder.create();
		dialog.show();
	}


}
