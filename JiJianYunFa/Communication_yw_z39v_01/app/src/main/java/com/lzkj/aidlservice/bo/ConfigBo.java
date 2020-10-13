package com.lzkj.aidlservice.bo;
/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月15日 上午11:23:20 
 * @version 1.0 
 * @parameter 配置文件对应的bo
 */
public class ConfigBo {
	private long flow;
	private int re;
	private int app;
	private int app_type;
	private boolean log;
	private int log_validity;
	/**
	 * @return the flow
	 */
	public long getFlow() {
		return flow;
	}
	/**
	 * @param flow the flow to set
	 */
	public void setFlow(long flow) {
		this.flow = flow;
	}
	/**
	 * @return the re
	 */
	public int getRe() {
		return re;
	}
	/**
	 * @param re the re to set
	 */
	public void setRe(int re) {
		this.re = re;
	}
	/**
	 * @return the app
	 */
	public int getApp() {
		return app;
	}
	/**
	 * @param app the app to set
	 */
	public void setApp(int app) {
		this.app = app;
	}
	/**
	 * @return the log
	 */
	public boolean isLog() {
		return log;
	}
	/**
	 * @param log the log to set
	 */
	public void setLog(boolean log) {
		this.log = log;
	}
	/**
	 * @return the log_validity
	 */
	public int getLog_validity() {
		return log_validity;
	}
	/**
	 * @param log_validity the log_validity to set
	 */
	public void setLog_validity(int log_validity) {
		this.log_validity = log_validity;
	}
	/**
	 * @return the app_type
	 */
	public int getApp_type() {
		return app_type;
	}
	/**
	 * @param app_type the app_type to set
	 */
	public void setApp_type(int app_type) {
		this.app_type = app_type;
	}
	
	
}
