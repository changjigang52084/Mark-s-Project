package com.lzkj.downloadservice.log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.lzkj.downloadservice.util.ConfigSettings;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.Helper;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;



/**
 * 日志缓存、写入磁盘、清理过期日志。
 *
 * @author lyhuang
 * @version 1.0 
 * @date 2016-1-11 上午10:52:16
 */
public class LogBuilder {
	private static final LogTag TAG = LogUtils.getLogTag(LogBuilder.class.getSimpleName(), true);
	/** 日志缓存大小，满则写入磁盘*/ 
	private static final int LOG_CACHE_QUEUE_SIZE = 10;
	/** 日志文件缓存大小*/ 
	private static final int MAX_LOG_FILE_SIZE = 1* 1024 * 1024;
	/** 同一天的日志文件最大数量*/ 
	private static final int MAX_LOG_FILE_INDEX_SIZE = 100;
	/** 同类型的日志文件最大数量*/ 
	private static final int MAX_LOG_FILE_TYPE_SIZE = 5;
	/** 日志文件名前缀*/ 
	public static final String LOG_PREFIX = "Eposter";
	/** 日志文件后缀*/ 
	private static final String LOG_SUFFIX = "log";
	/** 缓存日志信息是否可写入磁盘的flag */
	private AtomicBoolean canWriteToDisk = new AtomicBoolean(true);
	/** 日志内存缓存列表 */
	private List<String> logCacheQueue = new CopyOnWriteArrayList<String>();
	/** 日志写入的列表*/
	private List<String> logWriterQueue = new CopyOnWriteArrayList<String>();
	/** 日志类型*/ 
	private LogType logType;
	/** 同一天的日志文件下标*/ 
	private int index = 1;
	
	public LogBuilder (LogType logType){
		this.logType = logType;
	}
	
	/** 
	 * 添加日志到缓存队列中
	 * @param 
	 */ 
	void queueMessage(String message) {
		logCacheQueue.add(message);
		LogUtils.d(TAG, "queueMessage","logType:"+logType + ", logCacheQueue size:"+ logCacheQueue.size()+", message:"+ message);
	}
	
	/** 
	 * 
	 * 检查日志缓存队列是否满，满则往磁盘中写入日志 
	 */ 
	void checkForQueueOverflow() {
		if (logCacheQueue.size() >= LOG_CACHE_QUEUE_SIZE) {
			writeMessagesInTheQueue();
		}
	}
	
	
	
	/** 
	 * 缓存队列中日志写入磁盘
	 * 
	 * @param 
	 * @return 
	 */ 
	void writeMessagesInTheQueue() {
		if (canWriteToDisk() && canWriteToDisk.compareAndSet(true, false) && logCacheQueue.size() > 0) {
			Thread writerThread = new Thread(writerRunnable);
			writerThread.start();
		} else {
			LogUtils.d(TAG, "writeMessagesInTheQueue","logType:"+logType + ", current cannot do write log");
		}
	}
	
	/**
	 * 写入日志的Runnable
	 */
	private Runnable writerRunnable = new Runnable() {
		@Override
		public void run() {
			copyMessagesFromQueue();
			saveMessagesToDisk();
		}
	};
	
	
	/**
	 * 1.把缓存队列的日志复制到写入队列
	 * 2.清空存储队列
	 */
	private void copyMessagesFromQueue() {
		synchronized (logCacheQueue) {
			logWriterQueue.addAll(logCacheQueue);
			logCacheQueue.clear();
		}
	}
	
	/**
	 * 1.把写入队列中的日志写入磁盘
	 * 2.清空写入队列
	 */
	private void saveMessagesToDisk() {
		StringBuilder builder = new StringBuilder();
		for (String s : logWriterQueue) {
			builder.append(s);
		}
		LogUtils.d(TAG, "saveMessagesToDisk","logType:"+logType + ", writeMessage:"+ builder.toString());
		writeMessage(builder.toString());
		logWriterQueue.clear();
		canWriteToDisk.set(true);
	}
	
	/**
	 * 写日志到磁盘日志文件
	 * @param text 日志信息
	 */
	private void writeMessage(String text) {
		// set LogTactics 设置日志策略
		File logDirectory = new File(FileUtil.getInstance().getLogFolder());
		boolean flag = true;
		while (flag) {
			try {
				File logFile = new File(logDirectory, getLogFileName());
//				File logFile = new File(logDirectory, getLogFileName(index));
				if (logFile.length() < MAX_LOG_FILE_SIZE) {
					flag = false;
					FileOutputStream fos = new FileOutputStream(logFile, true);
					fos.write(text.getBytes());
					fos.close();
				} else {
					index ++;
					if (index > MAX_LOG_FILE_INDEX_SIZE) {
						index = 1;
						flag = false;
						LogUtils.d(TAG, "writeMessage","Out of max log file index size.");
					}

				}
			} catch (FileNotFoundException e) {
				flag = false;
				LogUtils.e(TAG, "writeMessage", e);
			} catch (IOException e) {
				flag = false;
				LogUtils.e(TAG, "writeMessage", e);
			} catch (Exception e) {
				flag = false;
				LogUtils.e(TAG, "writeMessage", e);
			}
		}
		cleanCacheLogFile();
	}
	
	/**
	 * 清除多余的缓存log
	 */
	private void cleanCacheLogFile() {
		File logDir = new File(FileUtil.getInstance().getLogFolder());
		File[] fileList = logDir.listFiles(filter);
		if (fileList != null && fileList.length > MAX_LOG_FILE_TYPE_SIZE) {
			Arrays.sort(fileList, comparator);
			List<File> list = Arrays.asList(fileList);
			list = list.subList(MAX_LOG_FILE_TYPE_SIZE, list.size());
			for (File f : list) {
				LogUtils.i(TAG, "cleanCacheLogFile"," delete log:"+ f.getName());
				f.delete();
			}
		}
	}
	
	/**日志文件排序*/ 
	private Comparator<File> comparator = new Comparator<File>() {
		
		@Override
		public int compare(File o1, File o2) {
			return -o1.getName().compareTo(o2.getName());
		}
	};
	
	/** 日志文件格式过滤器*/ 
	private FileFilter filter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			String regex = LOG_PREFIX + "_" + logType + "_" + "[0-9]{4}-[0-9]{2}-[0-9]{2}_[0-9]{3}" + "." + LOG_SUFFIX;
			return pathname.getName().matches(regex);
		}
	};
	
	/**
	 * 缓存目录是否可写
	 * @return true if the the media is mounted and can be written into
	 */
	private boolean canWriteToDisk() {
		return new File(FileUtil.getInstance().getSdcard()).canWrite();
	}

	/** 
	 * 获取日志文件名
	 * @param index 同一天的日志文件下标
	 * @return logFileName 日志文件名
	 */ 
//	private String getLogFileName(int index) {
//		String logFileName = LOG_PREFIX + "_" + logType + "_" + Util.getStringTimeToFormat("yyyy_MM_dd_HH") 
//				+ "_ ." + LOG_SUFFIX;
//		return logFileName;
//	}
	/** 
	 * 获取日志文件名
	 * @param index 同一天的日志文件下标
	 * @return logFileName 日志文件名
	 */ 
	private String getLogFileName() {
		String logFileName = LOG_PREFIX + "_" + logType + "_" + Helper.getStringTimeToFormat("yyyy_MM_dd_HH")  
				+ "_"+ ConfigSettings.getDeviceId() + "." + LOG_SUFFIX;
		return logFileName;
	}

	/** 日志枚举类型 */
	public enum LogType {
		/** 播放日志 */ PLAY_LOG, 
		/** 错误日志 */ERROR_LOG, 
		/** 设备运行日志 */ OPERATION_LOG
	}

}
