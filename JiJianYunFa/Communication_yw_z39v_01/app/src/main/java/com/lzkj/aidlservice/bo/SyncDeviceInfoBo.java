package com.lzkj.aidlservice.bo;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @date 创建时间：2016年1月12日 下午2:57:53
 * @version 1.0
 * @parameter 同步设备信息的bean
 */
public class SyncDeviceInfoBo {

	public String workTime;
	public String programList;
	public String volumn;
	public String serverTime;
	public String appInfo;

	/**
	 * 获取工作时间
	 * @return
	 */
	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	/**
	 * 获取节目列表
	 * @return
	 */
	public String getProgramList() {
		return programList;
	}

	public void setProgramList(String programList) {
		this.programList = programList;
	}
	/**
	 * 获取音量
	 * @return
	 */
	public String getVolumn() {
		return volumn;
	}

	public void setVolumn(String volumn) {
		this.volumn = volumn;
	}
	/**
	 * 获取服务器端时间
	 * @return
	 */
	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	/**
	 * 获取APP信息
	 * @return
	 */
	public String getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}

}
