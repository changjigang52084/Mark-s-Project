package com.lzkj.downloadservice.util;

import java.io.File;

import android.content.Context;

import com.lzkj.downloadservice.R;
import com.lzkj.downloadservice.util.FileUtil.LogType;

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
//		ErrorLogUtil.writerLog(ex);
		String erroLog = FileUtil.setErrorLog(cxt.getString(R.string.app_name), 
				LogUtils.getStackTraceString(LogType.ERROR_LOG, ex));
		FileUtil.getInstance().writerLog(erroLog, 
				FileUtil.getInstance().getLogFolder() + File.separator + 
				FileUtil.getLogFileName(LogType.ERROR_LOG));
	}
}
