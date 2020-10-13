package com.lzkj.aidlservice.manager;

import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzmr.client.core.util.LogUtils;
import com.lzmr.client.core.util.LogUtils.LogTag;

/**
 * 汇报终端开机、关机状态的工具类
 *
 * @author lyhuang
 * @date 2016-2-25 下午2:41:52
 */
public class ReportDeviceStatusManager  implements IRequestCallback{
	private static final LogTag LOG_TAG = LogUtils.getLogTag(ReportDeviceStatusManager.class.getSimpleName(), true);
	public static final String DEVICE_STATE_STARTUP = "on";
	public static final String DEVICE_STATE_SHUTDOWN = "off";
	
	private volatile static ReportDeviceStatusManager ins;
	
	private ReportDeviceStatusManager(){
	}
	
	public static ReportDeviceStatusManager get(){
		if(null == ins){
			synchronized (ReportDeviceStatusManager.class) {
				if (null == ins) {
					ins = new ReportDeviceStatusManager();
				}
			}
		}
		return ins;
	}
	
	/**
	 * 汇报开关机状态
	 * @param deviceState 开关机状态  开机：on  关机：off
	 * @return
	 */
	public void reportDeviceState(String deviceState){
		HttpRequestBean httpRequestBean = new HttpRequestBean();
		String requestUrl = null;
		
		if (DEVICE_STATE_STARTUP.equals(deviceState)) {
			LogUtils.i(LOG_TAG, "reportDeviceState", "Device starup.");
			requestUrl = HttpConfigSetting.getReportDeviceStartupUrl(ConfigSettings.getDid());
		} else if (DEVICE_STATE_SHUTDOWN.equals(deviceState)) {
			LogUtils.i(LOG_TAG, "reportDeviceState", "Device shutdown.");
			requestUrl = HttpConfigSetting.getReportDeviceShutdownUrl(ConfigSettings.getDid());
		}
		
		if(null == requestUrl){
			LogUtils.e(LOG_TAG, "reportDeviceState", "RequestUrl is null.");
			return;
		}
		
		httpRequestBean.setRequestUrl(requestUrl);
		httpRequestBean.setRequestCallback(this);
		httpRequestBean.setRequestRestry(5);
		httpRequestBean.setRequestTag("reportDeviceState");
		HttpUtil.newInstance().postRequest(httpRequestBean);
	}


	@Override
	public void onSuccess(String result, String httpTag, String requestUrl) {
		LogUtils.d(LOG_TAG, "onSuccess", "result: "+result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
		//SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
	}

	@Override
	public void onFaile(String errMsg, String httpTag, String requestUrl) {
		LogUtils.d(LOG_TAG, "onFaile", "errMsg: "+errMsg);
	}
}
