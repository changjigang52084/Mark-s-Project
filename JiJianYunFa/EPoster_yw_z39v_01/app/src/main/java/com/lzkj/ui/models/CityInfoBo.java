package com.lzkj.ui.models;

/**
 * 城市信息javabean
 *
 * @author lyhuang
 * @date 2016-1-16 下午3:16:20
 */
public class CityInfoBo {

	/**
	 * 城市名
	 */
	private String cityName;
	/**
	 * 省级名 
	 */
	private String provinceName;
	/**
	 * 城市代码
	 */
	private String cityCode;
	
	/**
	 * 邮政
	 */
	private String zipCode;
	
	/**
	 * 电话区号
	 */
	private String telAreaCode;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getTelAreaCode() {
		return telAreaCode;
	}

	public void setTelAreaCode(String telAreaCode) {
		this.telAreaCode = telAreaCode;
	}

	@Override
	public String toString() {
		return "CityInfoBo [cityName=" + cityName + ", provinceName="
				+ provinceName + ", cityCode=" + cityCode + ", zipCode="
				+ zipCode + ", telAreaCode=" + telAreaCode + "]";
	}
	
	

}
