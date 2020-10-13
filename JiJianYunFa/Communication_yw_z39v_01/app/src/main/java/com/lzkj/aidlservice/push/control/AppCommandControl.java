package com.lzkj.aidlservice.push.control;

import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandApplicationSetup;
import com.lzkj.aidlservice.manager.DownloadManager;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月14日 下午7:49:11 
 * @version 1.0 
 * @parameter 针对App相关的控制命令 
 */
public class AppCommandControl {
	public static final LogTag TAG_LOG = LogUtils.getLogTag(AppCommandControl.class.getSimpleName(), true);
	
	/**
	 * 更新APP
	 * @param adpressDataPackage
	 */
	void updateApp(AdpressDataPackage adpressDataPackage) {
		try {
			SharedUtil.newInstance().setString(SharedUtil.INSTALL_APP_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
			
			CommandApplicationSetup appSetup = adpressDataPackage.getData();
			downloadApp(appSetup.getApp().getPath() + Constant.SPLIT_APP_PATH + appSetup.getApp().getMd5());
			// Method: updateApp, Message: app getName:Z39V非楼层Launcher1.0.50.20180919.yw.beta,getMd5:luDLgQxXvzHrMiBFiTiikHh46NSA
			LogUtils.d(TAG_LOG, "updateApp", "app getName:" + appSetup.getApp().getName()+",getMd5:" + appSetup.getApp().getMd5());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据app path进行下载
	 * @param appPath
	 */
	private void downloadApp(String appPath) {
		List<String> downloadList = new ArrayList<String>();
		downloadList.add(appPath);
		DownloadManager.newInstance().addDownloadList(downloadList, Constant.DOWNLOAD_APP, "-1");
	}
}
