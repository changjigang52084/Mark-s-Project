package com.lzkj.downloadservice.util;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baize.adpress.core.protocol.dto.AdpressDeviceTrafficReportItemPackage;
import com.baize.adpress.core.protocol.dto.AdpressDeviceTrafficReportPackage;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.bean.FlowBo;
import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.interfaces.IRequestCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月9日 下午8:13:43 
 * @version 1.0 
 * @parameter  流量管理类
 */
public class FlowManager implements IRequestCallback {
	private static final LogUtils.LogTag TAG = LogUtils.getLogTag(FlowManager.class.getSimpleName(), true);
	/**默认最大上限流量为1G 单位M, 值为-1则表示不限制流量*/
	public static long DEFAULT_MAX_FOLW = 1024;
	private static final String CURRENT_DATE = DateUtil.getInstance().getCurrentDate(null);
	private FlowManager() {}
	private static volatile FlowManager flowManager;
	public static FlowManager getInstance() {
		if (null == flowManager) {
			synchronized (FlowManager.class) {
				if (null == flowManager) {
					flowManager = new FlowManager();
				}
			}
		}
		return flowManager;
	}
	
	public synchronized boolean addDownloadFlow(long flowValue) {
		SQLiteManager.getInstance().insterFlow(flowValue, 0, CURRENT_DATE);
		long downloadFlow = ShreadUtil.newInstance().getLong(ShreadUtil.DOWNLOAD_FLOW_DAY_KEY);
		downloadFlow += flowValue;
		return ShreadUtil.newInstance().putLong(ShreadUtil.DOWNLOAD_FLOW_DAY_KEY, downloadFlow);
	}
	
	public synchronized boolean addUploadFlow(long flowValue) {
		SQLiteManager.getInstance().insterFlow(0, flowValue, CURRENT_DATE);
		long uploadFlow = ShreadUtil.newInstance().getLong(ShreadUtil.UPLOAD_FLOW_DAY_KEY);
		uploadFlow += flowValue;
		return ShreadUtil.newInstance().putLong(ShreadUtil.UPLOAD_FLOW_DAY_KEY, uploadFlow);
	}

	/**
	 * 获取下载流量
	 * @return
	 */
	public long getDownloadFlow() {
		return ShreadUtil.newInstance().getLong(ShreadUtil.DOWNLOAD_FLOW_DAY_KEY);
	}

	/**
	 * 获取上传流量
	 * @return
     */
	public long getUploadFlow() {
		return ShreadUtil.newInstance().getLong(ShreadUtil.UPLOAD_FLOW_DAY_KEY);
	}
	
	public boolean initFlow() {
		ShreadUtil.newInstance().putLong(ShreadUtil.DOWNLOAD_FLOW_DAY_KEY, 0);
		
		return ShreadUtil.newInstance().putLong(ShreadUtil.UPLOAD_FLOW_DAY_KEY, 0);
	}

	/**
	 * 获取本机流量上限
	 * @return
	 */
	public long getMaxFlow() {
		long maxFlow = ShreadUtil.newInstance().getLong(ShreadUtil.MAX_FOLW_KEY);
		return maxFlow;
	}
	/**
	 * 设置本机流量上限
	 * @return
	 */
	public boolean setMaxFlow(long maxFlow) {
		return ShreadUtil.newInstance().putLong(ShreadUtil.MAX_FOLW_KEY, maxFlow);
	}
	
	/**
	 * 获取使用的流量值
	 * @return
	 */
	public long getUsedFlow() {
		return ShreadUtil.newInstance().getLong(ShreadUtil.USE_FOLW_KEY);
	}
	
	/**
	 * 获取可用流量数 单位B
	 * @return
	 */
	public long getAvailableFlow() {
		long maxFlow  = getMaxFlow();
		if (0 == maxFlow || maxFlow == -1) {
			maxFlow = DEFAULT_MAX_FOLW * DEFAULT_MAX_FOLW * DEFAULT_MAX_FOLW * DEFAULT_MAX_FOLW;
			return maxFlow;
		}
		long usedFlow = getDownloadFlow() + getUploadFlow();
		return (maxFlow - usedFlow);
	}

	/***
	 * 汇报流量到服务器
	 */
	public void uploadFolwToServer() {
		AdpressDeviceTrafficReportPackage trafficReportPackage = new
				AdpressDeviceTrafficReportPackage();
		String did = ConfigSettings.getDeviceId();
		if (!TextUtils.isEmpty(did)) {
			try {
				trafficReportPackage.setDeviceId(Long.getLong(did));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			String month = DateUtil.getInstance().getCurrentDate("yyyyMM");

			trafficReportPackage.setStartTime(DateUtil.getInstance().getCurrentMonthFirstDay());
			trafficReportPackage.setEndTime(System.currentTimeMillis());
			trafficReportPackage.setTrafficDown(getDownloadFlow());
			trafficReportPackage.setTrafficUp(getUploadFlow());
			trafficReportPackage.setTrafficDailyReports(getAdpressDeviceTrafficReportItemPackages());
			trafficReportPackage.setId(month);

			String requestParm = null;
			try {
				requestParm = JSON.toJSONString(trafficReportPackage);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			reportFlow(requestParm);
		}
	}

	private List<AdpressDeviceTrafficReportItemPackage> getAdpressDeviceTrafficReportItemPackages() {
		List<AdpressDeviceTrafficReportItemPackage> itemPackageList = new ArrayList<>();
		String yesterday = DateUtil.getInstance().getCurrentMonthDate(-1, null);
		String yesterdayStartTime = DateUtil.getInstance().getCurrentMonthDate(-1, "yyyyMMdd");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		long yesterdayStartTimeLong = 0;
		long yesterdayEndTimeLong = 0;
		try {
			yesterdayStartTimeLong = simpleDateFormat.parse(yesterdayStartTime).getTime();
			yesterdayEndTimeLong = simpleDateFormat.parse(
					DateUtil.getInstance().getCurrentDate(null)).getTime() - 1;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		FlowBo flowBo = SQLiteManager.getInstance().queryFlowBo(yesterday);
		if (null != flowBo) {
			AdpressDeviceTrafficReportItemPackage itemPackage
					= new AdpressDeviceTrafficReportItemPackage();
			itemPackage.setId(yesterday);
			itemPackage.setStartTime(yesterdayStartTimeLong);
			itemPackage.setEndTime(yesterdayEndTimeLong);
			itemPackage.setTrafficDown(flowBo.downloadFlow);
			itemPackage.setTrafficUp(flowBo.uploadFlow);

			itemPackageList.add(itemPackage);
		}
		return itemPackageList;
	}

	/**
	 * 汇报流量
	 * @param requestParm
     */
	private void reportFlow(String requestParm) {
		//发送请求
		LogUtils.e(TAG,"reportFlow","report msg:"+requestParm);
		HttpRequestBean httpRequestBean = new HttpRequestBean();
		httpRequestBean.setRequestCallback(this);
		httpRequestBean.setRequestUrl(HttpUtil.getReportTrafficUrl());
		httpRequestBean.setRequestParm(requestParm);
		httpRequestBean.setRequestTag(FlowManager.class.getSimpleName()
				+ System.currentTimeMillis());
		HttpUtil.newInstance().postRequest(httpRequestBean);
		SQLiteManager.getInstance().insterHttpBo(httpRequestBean, "POST");
	}

	@Override
	public void onSuccess(String result, String httpUrl, String requestTag) {
		LogUtils.e(TAG,"onSuccess","report msg:"+result);
		SQLiteManager.getInstance().delHttpBo(httpUrl, requestTag);
	}

	@Override
	public void onFaile(String errMsg, String httpUrl, String requestTag) {
		LogUtils.e(TAG,"onFaile","report msg:"+errMsg);
	}
}
