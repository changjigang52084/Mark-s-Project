package com.lzkj.launcher.util;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.launcher.MainActivity;
import com.lzkj.launcher.R;
import com.lzkj.launcher.log.LogBuilder.LogType;
import com.lzkj.launcher.log.LogManager;

/**
 * 应用崩溃捕获错误日志
 *
 * @author lyhuang
 * @date 2016-1-26 上午9:35:53
 */
public class AppErrorHandler implements Thread.UncaughtExceptionHandler {

	private Context cxt;
	private Thread.UncaughtExceptionHandler mDefaultHandler;  
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handlerException(ex);
		mDefaultHandler.uncaughtException(thread, ex);
		
    }  
	public void init(Context cxt) {
		this.cxt = cxt;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
	}
	
	private void handlerException(Throwable ex) {
		//程序发生异常闪退的时候会调用这里的方法
		LogManager.get().writeErrorLog(LogManager.get().
				setErrorLog(StringUtil.getString(R.string.app_name),
							LogUtils.getStackTraceString(LogType.PLAY_LOG, ex)));
		 Intent intent = new Intent(cxt, MainActivity.class);  
	     PendingIntent restartIntent = PendingIntent.getActivity(cxt, 0, intent,    
	                Intent.FLAG_ACTIVITY_NEW_TASK);                                                 
	    AlarmManager mgr = (AlarmManager)cxt.getSystemService(Context.ALARM_SERVICE);    
	    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, restartIntent); // 5秒钟后重启应用  
	}
	
}
