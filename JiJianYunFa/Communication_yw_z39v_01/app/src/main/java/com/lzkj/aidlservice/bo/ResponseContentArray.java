/**
 * 
 */
package com.lzkj.aidlservice.bo;

import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2015 BaiZe.com. All rights reserved.
 * 
 * @author zhanghailiang
 * @location Shenzhen.China
 * @date 2015年7月31日
 * 
 */
public class ResponseContentArray {
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
	private List<String> data;
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
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public Map<String, Object> getBundle() {
		return bundle;
	}
	public void setBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
	}
}
