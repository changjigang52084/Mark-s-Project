package com.lzkj.aidlservice.bo;

import com.baize.adpress.core.protocol.dto.AdpressDeviceValidatePackage;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月24日 下午12:14:57 
 * @version 1.0 
 * @parameter  
 */
public class AdpressDeviceValidatePackageSub {
	private String getuiId;
	private String mac;
	private String systemType;
	
	/**
	 * @return the getuiId
	 */
	public String getGetuiId() {
		return getuiId;
	}

	/**
	 * @param getuiId the getuiId to set
	 */
	public void setGetuiId(String getuiId) {
		this.getuiId = getuiId;
	}

	/**
	 * @return the mac
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * @param mac the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * @return the systemType
	 */
	public String getSystemType() {
		return systemType;
	}

	/**
	 * @param systemType the systemType to set
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	

}
