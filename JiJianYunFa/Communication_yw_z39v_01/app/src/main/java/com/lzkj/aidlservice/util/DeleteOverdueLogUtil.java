package com.lzkj.aidlservice.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.lzkj.aidlservice.util.LogUtils.LogTag;


/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月27日 下午3:42:07 
 * @version 1.0 
 * @parameter  删除过期的日志
 */
public class DeleteOverdueLogUtil {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(DeleteOverdueLogUtil.class.getSimpleName(), true);
	/**
	 * 删除过期的日志文件
	 */
	public static void deleteOverdueLog() {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				try {
					File logFloder = new File(FileUtile.getInstance().getLogFolder());//获取log文件夹
					//获取文件夹下面所有的文件
					File[] logFiles = logFloder.listFiles();
					int currentDate = Integer.parseInt(getCurrentFrontDate(ConfigSettings.LOG_OVERDUE_DATE));//获取当前时间前五天
					for (File logFile : logFiles) {
						String logName[] = logFile.getName().split("_");//日志名称
						if(logName.length > 0){
//							String appName = logName[0];
//							if(LogBuilder.LOG_PREFIX.equals(appName)){
								String logDate = logName[3] + logName[4] + logName[5];
								int date = Integer.parseInt(logDate);//获取当前日志的时间
								LogUtils.d(TAG_LOG, "deleteOverdueLog", "currentDate：" + currentDate 
										+ ",date:" + date + "del file name:" + logFile.getName());
								if (currentDate > date) {
									logFile.delete();
								}
//							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 获取当前时间的前n天日期
	 * @param front
	 * 			前多少天负数,后多少天正数,例如:当前日期20150928 传入-2则返回20150926,传入2则返回20150930
	 * @return
	 */
	private static String getCurrentFrontDate(int front) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();  
        c.add(Calendar.DATE, front);  
        String preMonday = sdf.format(c.getTime());
        return preMonday;
	}
}

