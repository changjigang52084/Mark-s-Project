package com.lzkj.aidlservice.bo;

import java.util.List;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年1月21日 上午10:39:04 
 * @version 1.0 
 * @parameter  同步节目列表(今天和明天的节目)
 */
public class Programs {
	/**
	 * 是否成功
	 */
	private boolean success;
	/**
	 * 错误信息提示
	 */
	private String message;
	/**
	 * 一组节目列表
	 */
	private List<Program> data;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Program> getData() {
		return data;
	}
	public void setData(List<Program> data) {
		this.data = data;
	}
	
}
