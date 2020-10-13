package com.lzkj.aidlservice.api.interfaces;
/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月19日 上午11:43:08 
 * @version 1.0 
 * @parameter  reids 接口回调
 */
public interface IRedisCallback {
	/**
	 * redis 读取得到的value
	 * @param value
	 * 			结果值
	 */
	void setValue(String key, String value);
	
	void error(String key);
}
