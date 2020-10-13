package com.lzkj.aidlservice.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.api.report.UploadAppInfoRunnable;
import com.lzkj.aidlservice.api.sync.RequestSyncAppVersionRunnable;
import com.lzkj.aidlservice.api.sync.RequestSyncDeviceInfo;
import com.lzkj.aidlservice.api.sync.RequestSyncProgramRunnable;
import com.lzkj.aidlservice.api.sync.RequestSyncWeather;
import com.lzkj.aidlservice.manager.ValidationDeviceManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.DeviceFlowUtle;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.ParsePrmUitl;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月13日 上午11:20:42 
 * @version 1.0 
 * @parameter  使用adb 进行测试的receive
 */
public class TestReceiver extends BroadcastReceiver {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(TestReceiver.class.getSimpleName(), true);
	/**当前广播拦截的action**/
	private static final String ACTION = "com.lzkj.aidlservice.test.TEST_RECEIVER_ACTION";
	
	private static final String TYPE = "type";
	private static final String UPDATE_FOLW = "folw";
	private static final int SYCN_VALIDATION = 5;
	private static final int UPLOAD_APP_INFO = 6;
	private static final int UPLOAD_APP_VERSION = 7;
	private static final int SYCN_WEATHER = 4;
	private static final int SYCN_DEVICE = 3;
	private static final int SYCN_PRM = 2;
	private static final int FOLW = 0;
	
	
	private static final int SCOKET = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(TYPE, -1);
		switch (type) {
		case FOLW:
			updateFolw(intent);
			break;
		case SCOKET:
			startSocket(intent);
			break;
		case SYCN_PRM:
			ThreadPoolManager.get().addRunnable(new RequestSyncProgramRunnable(null));
			break;
		case SYCN_VALIDATION:
			//重新发布授权请求
			ValidationDeviceManager.get().validationDevice(ConfigSettings.MAC_ADDRESS, null);
			break;
		case SYCN_DEVICE:
			RequestSyncDeviceInfo requestSyncDeviceInfo = new RequestSyncDeviceInfo(null);
			ThreadPoolManager.get().addRunnable(requestSyncDeviceInfo);
			break;
		case SYCN_WEATHER:
			ThreadPoolManager.get().addRunnable(new Runnable() {
				@Override
				public void run() {
					new RequestSyncWeather().syncWeather();
				}
			});
			break;
		case UPLOAD_APP_INFO:
			ThreadPoolManager.get().addRunnable(new UploadAppInfoRunnable());
			break;
		case UPLOAD_APP_VERSION:
			ThreadPoolManager.get().addRunnable(new RequestSyncAppVersionRunnable("TestReceiver"));
			break;
		default:
			break;
		}
	}
	
	private void startSocket(final Intent intent) {
	}
	
	private void updateFolw(final Intent intent) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				long newFolw = intent.getLongExtra(UPDATE_FOLW, -1);
				if (-1 != newFolw) {
					DeviceFlowUtle.getInstance().sendUpdateMaxFlow(newFolw, false);
					SharedUtil.newInstance().setLong(SharedUtil.MAX_FOLW_KEY, newFolw);
					//获取当前已使用的流量进行比较  
					long useFolw = ConfigSettings.getUsedFolw();
					long unuseFolw = newFolw - useFolw;//剩余流量
					LogUtils.d(TAG_LOG, "updateFolw", "newFolw : " + newFolw + "M," + "useFolw : " +
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
