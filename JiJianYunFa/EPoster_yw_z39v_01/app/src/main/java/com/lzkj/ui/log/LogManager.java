package com.lzkj.ui.log;

import java.io.File;

import com.lzkj.ui.log.LogBuilder.LogType;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.FileStore;

/**
 * 程序日志管理
 * @author lyhuang
 * @version 1.0 
 * @date 2016-1-11 下午5:59:59
 */
public class LogManager {
private static LogManager ins;
	private String wrap = "\r\n";
	/** 播放日志*/ 
	private LogBuilder playLogInfo = new LogBuilder(LogType.PLAY_LOG);
	/** 运行日志*/ 
	private LogBuilder operationLogInfo = new LogBuilder(LogType.OPERATION_LOG);
	/** 错误日志*/ 
	private LogBuilder errorLogInfo = new LogBuilder(LogType.ERROR_LOG);
	
	public static LogManager get() {
		if (ins == null) {
			ins = new LogManager();
		}
		return ins;
	}
	
	private LogManager() {}
	
	/**
	 * 加入一条播放信息到缓存队列
	 * @param logMessage 播放信息 
	 */
	public void insertPlayMessage(String logMessage) {
		//播放日志的格式: 节目id_素材名称_开始播放时间(毫秒)_结束播放时间(毫秒L)_应播时间(秒)
		if (playLogInfo != null) {
			playLogInfo.queueMessage(logMessage + wrap);
			playLogInfo.checkForQueueOverflow();
		}
	}
	/**
	 * 加入一条设备运行信息到缓存队列
	 * @param logMessage 设备运行信息 
	 */
	public void insertOperationMessage(String logMessage) {
		//设备运行日志的格式: 网络断开(错误代码)_发生的时间(毫秒)_msg.  网络连接(代码)_发生时间（毫秒）. 音量变化_本来的音量_改变后的音量_时间
		if (operationLogInfo != null) {
			operationLogInfo.queueMessage(logMessage + wrap);
			operationLogInfo.checkForQueueOverflow();
		}
	}
	/**
	 * 加入一条错误信息到缓存队列
	 * @param errorMessage 错误信息 
	 */
	public void writeErrorLog(String errorMessage) {
		//错误日志的格式： #&#应用程序名称|错误类型|错误发生的时间(毫秒)|系统版本号|程序版本号|错误的详细信息
		if (errorLogInfo != null) {
			errorLogInfo.queueMessage(errorMessage + wrap);
			errorLogInfo.writeMessagesInTheQueue();
		}
	}
	
	/**
	 * 删除指定的日志
	 */
	public void deleteLog(String path) {
		File file = new File(FileStore.getInstance().getLogFolderPath() + "/" + path);
		if (file.exists())
			file.delete();
	}
	
	/**
	 * 程序退出，把所有的日志都写到文件中
	 */
	public void flush() {
		if (playLogInfo != null) {
			playLogInfo.writeMessagesInTheQueue();
		}
		
		if (operationLogInfo != null) {
			operationLogInfo.writeMessagesInTheQueue();
		}
		
		if (errorLogInfo != null) {
			errorLogInfo.writeMessagesInTheQueue();
		}
	}
	
	/**
	 * 获取错误日志格式
	 * @param appName 应用名
	 * @param errorMessage 错误信息
	 * @return 日志格式
	 */
	public String setErrorLog(String appName,String errorMessage){
		return Constants.ERROR_SPLIT + appName + Constants.SPLIT + errorMessage;
	}
	
	/**
	 * 获取播放日志格式
	 * @param programId 节目id
	 * @param materialName 素材名称
	 * @param playTime 播放时间（秒）
	 * @return 日志格式
	 */
	public String setPlayLog(int programId,String materialName,int playTime){
		return programId + Constants.SPLIT + materialName + Constants.SPLIT + playTime;
	}
	
}
