package com.lzkj.aidlservice.api.report;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.AdpressDeviceWorkingStateReportPackage;
import com.baize.adpress.core.protocol.dto.DeviceApplicationReportPackage;
import com.lzkj.aidlservice.api.impl.ReportDeviceWorkImpl;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年10月10日 上午11:40:06 
 * @version 1.0 
 * @parameter  上传app信息到服务器
 */
public class UploadAppInfoRunnable implements Runnable{

	@Override
	public void run() {
		uploadAppInfo();
	}	
	
	/**
	 * 上传app info
	 */
	private void uploadAppInfo() {
		ReportDeviceWorkImpl reportDeviceWorkHandler = new ReportDeviceWorkImpl(getRequestParam());
		reportDeviceWorkHandler.reportDeviceWorkMsg();
	}
	
	private String getRequestParam() {
		AdpressDeviceWorkingStateReportPackage reportPackage = new AdpressDeviceWorkingStateReportPackage();
		List<DeviceApplicationReportPackage> deviceApplicationReportPackages = new ArrayList<DeviceApplicationReportPackage>();
		Context context = CommunicationApp.get();
		PackageManager packageManager = context.getPackageManager();
		Map<Integer, String> appIdMap = getAppIdMap();
		List<String> pckList = getPckList();
		for (String pck : pckList) {
			DeviceApplicationReportPackage deviceApplicationReportPackage = new DeviceApplicationReportPackage();
			PackageInfo appInfo = AppUtil.getAppInfo(context, pck);
			if (appInfo != null) {
				deviceApplicationReportPackage.setVersionCode(appInfo.versionCode);
				deviceApplicationReportPackage.setPkg(pck);
				deviceApplicationReportPackage.setVersion(appInfo.versionName);
				deviceApplicationReportPackage.setName(appInfo.applicationInfo.loadLabel(packageManager).toString());
			}
			deviceApplicationReportPackages.add(deviceApplicationReportPackage);
		}
		reportPackage.setApplications(deviceApplicationReportPackages);

		return JSON.toJSONString(reportPackage);
	}

	private Map<Integer, String> getAppIdMap() {
		Map<Integer, String> appIdMap = new HashMap<Integer, String>();
		appIdMap.put(0, CommunicationApp.get().getPackageName());
		if (ConfigSettings.isInstallDSplugAndSocail) {
			appIdMap.put(0, Constant.UI_PKG);
			appIdMap.put(0, Constant.MALL_POSTER_UI_PKG);
		} else {
			if (ConfigSettings.CURRENT_APP == HttpConstants.DSPLUG_APP) {
				appIdMap.put(0, Constant.UI_PKG);
			} else {
				appIdMap.put(0, Constant.MALL_POSTER_UI_PKG);
			}
		}
		appIdMap.put(0, Constant.DOWNLOAD_PKG);
		appIdMap.put(0, Constant.LAUNCHER_PKG);
		return appIdMap;
	}

	/**
	 * 获取应用包名列表
	 * @return
	 */
	private List<String> getPckList() {
		List<String> pckList = new ArrayList<String>();
		pckList.add(CommunicationApp.get().getPackageName());
		pckList.add(Constant.UI_PKG);
		pckList.add(Constant.DOWNLOAD_PKG);
		pckList.add(Constant.LAUNCHER_PKG);
		return pckList;
	}
}
