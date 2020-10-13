package com.lzkj.ui.log;

import java.util.concurrent.ConcurrentHashMap;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年3月21日 下午9:03:54 
 * @version 1.0 
 * @parameter  记录播放日志的类
 */
public class RecordPlayLogHandler {
	private static final LogTag TAG = LogUtils.getLogTag(RecordPlayLogHandler.class.getSimpleName(), true);
	/**key为素材key,value为日志*/
	private static ConcurrentHashMap<Long, String> playLogMap = new ConcurrentHashMap<Long, String>();
	/**结束日期占位符**/
	private static final String END_TIME_SPLIT = "#$#";
	/**
	 * 添加播放日志
	 * @param currentMaterial
	 * 			当前正在播放的素材对象
	 */
	public static void addPlayLog(Material currentMaterial) {
		long startPlayTime = System.currentTimeMillis();//开始播放的时间
		Program program = ProgramPlayManager.getInstance().getCurrentProgram();
		if (program == null || null == currentMaterial) {
			LogUtils.d(TAG, "recordPlayLog", "program is null or currentMaterial is null");
			return;
		}
		StringBuffer buffer = new StringBuffer(10);
		buffer.append(program.getKey());
		buffer.append(Constants.SPLIT);
		buffer.append(currentMaterial.getKey());
		buffer.append(Constants.SPLIT);
		buffer.append(FileStore.getInstance()
				.getImageFilePath(FileStore.getFileName(currentMaterial.getU())));
//		buffer.append(currentMaterial.getN());
		buffer.append(Constants.SPLIT);
		buffer.append(startPlayTime);
		buffer.append(Constants.SPLIT);
		buffer.append(END_TIME_SPLIT);
		buffer.append(Constants.SPLIT);
		buffer.append(currentMaterial.getD());
		LogUtils.d(TAG, "addPlayLog", "log:" + buffer.toString());
		playLogMap.put(currentMaterial.getKey(), buffer.toString());
	}
	
	/**
	 * 播放结束记录当前播放的素材
	 * @param currentMaterial
	 * 			当前正在播放的素材对象
	 */
	public static void writPlayLog(final Material currentMaterial) {
		if (null == currentMaterial) {
			LogUtils.d(TAG, "writPlayLog", "currentMaterial is null");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				long endPlayTime = System.currentTimeMillis();//结束播放的时间
				try {
					if (playLogMap.containsKey(currentMaterial.getKey())) { 
						String playLog = playLogMap.get(currentMaterial.getKey()).replace(END_TIME_SPLIT, String.valueOf(endPlayTime));
						LogUtils.d(TAG, "writPlayLog", "log:" + playLog);
						LogManager.get().insertPlayMessage(playLog);
						playLogMap.remove(currentMaterial.getKey());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}
