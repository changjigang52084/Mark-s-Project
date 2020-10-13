package com.lzkj.aidlservice.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.push.control.DeviceCommandControl;
import com.lzkj.aidlservice.util.Command;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.ParsePrmUitl;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月1日 下午7:24:17 
 * @version 1.0 
 * @parameter  流量控制处理类
 */
public class FlowLimitHandler {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(FlowLimitHandler.class.getSimpleName(), true);
	public static final String ACTION = "com.lzkj.downloadservice.receiver.UPDATE_FLOW_ACTION";
	//单位M
	public static final String FLOW_KEY = "flow";
	//communication用的流量超过200M就发送一次给download 单位M
	public static final String USE_FLOW_KEY = "useFlow";
	public void updateFlow(final long maxFlow) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				if (-1 != maxFlow) {
					sendUpdateMaxFlow(maxFlow);
					SharedUtil.newInstance().setLong(SharedUtil.MAX_FOLW_KEY, maxFlow);
					//获取当前已使用的流量进行比较  
					long useFolw = ConfigSettings.getUsedFolw();
					long unuseFolw = maxFlow - useFolw;//剩余流量
					LogUtils.d(TAG_LOG, "updateFolw", "newFolw : " + maxFlow + "M," + "useFolw : " +
					+ useFolw + "M" + ", unuseFolw: " + unuseFolw + "M" );
					if (unuseFolw > 0) {//1.如果上限的流量比已使用的流量大
						 //2.则判断本地是否有未下载完成的节目
						File folder = new File(FileUtile.getInstance().getWaitDownloadPrmFolder());
						if (folder.exists()) {
							File[] waitDownloadPrmList = folder.listFiles();
							if (waitDownloadPrmList.length > 0) {
								//3.获取节目大小判断节目是否可以下载
								downloadWaitPrm(waitDownloadPrmList, unuseFolw, useFolw);
							}
						}
					}
				}	
			}
		});
	}
	
	private void sendUpdateMaxFlow(long maxFlow) {
		Intent intent = new Intent(ACTION);
		intent.putExtra(FLOW_KEY, maxFlow);
		CommunicationApp.get().sendBroadcast(intent);
	}
	
	private void downloadWaitPrm(File[] waitDownloadPrmList, long unuseFolw, long useFolw) {
		LogUtils.d(TAG_LOG, "downloadWaitPrm", "unuseFolw : " + unuseFolw);
		long newDownloadUseFolw = 0;//重新下载使用的流量
		List<Program> newDownloadPrm = new ArrayList<Program>();
		for (File file : waitDownloadPrmList) {
			StringTokenizer stringTokenizer = new StringTokenizer(file.getName(), Constant.SPLIT_PATH);
			stringTokenizer.nextToken();
			float prmTotal = Float.valueOf(stringTokenizer.nextToken());
			long availableFolw = unuseFolw - newDownloadUseFolw;
			if (availableFolw > 0 && availableFolw > prmTotal) {
				newDownloadUseFolw += prmTotal;
				String pramJson = FileUtile.readPrmToFile(file);
				newDownloadPrm.add(JSON.parseObject(pramJson, Program.class));
				file.delete();
				LogUtils.d(TAG_LOG, "downloadWaitPrm", "del file : " + file.getAbsolutePath());
			}
			stringTokenizer = null;
		}
		long useTotalFolw = useFolw + newDownloadUseFolw;
//		SharedUtil.newInstance().setLong(SharedUtil.USE_FOLW_KEY, useTotalFolw);
		LogUtils.d(TAG_LOG, "downloadWaitPrm", "newDownloadUseFolw : " + newDownloadUseFolw + "M" + ",useFolw : "
				+ useFolw + "M" + ", unuseFolw: " + unuseFolw + "M" +", useTotalFolw: " + useTotalFolw);
		ParsePrmUitl.savePrmAndDownloads(newDownloadPrm);
	}
}
