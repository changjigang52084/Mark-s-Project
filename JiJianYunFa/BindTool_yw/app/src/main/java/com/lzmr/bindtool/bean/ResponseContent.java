/**
 * 
 */
package com.lzmr.bindtool.bean;

import java.util.Map;

/**
 * Copyright (c) 2015 BaiZe.com. All rights reserved.
 * 
 * @author zhanghailiang
 * @location Shenzhen.China
 * @date 2015年7月31日
 * 
 */
public class ResponseContent {
	/**
	 * 执行是否成功
	 */
	private boolean success;
	/**
	 * 响应携带消息
	 */
	private String message;
	/**
	 * 响应携带数据
	 */
	private Object data;
	/**
	 * 响应绑定参数
	 */
	private Map<String, Object> bundle;
	
	/* Setter&Getter Method */
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
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Map<String, Object> getBundle() {
		return bundle;
	}
	public void setBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
	}
}
