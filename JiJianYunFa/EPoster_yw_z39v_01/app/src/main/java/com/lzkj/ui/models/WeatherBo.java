package com.lzkj.ui.models;

/**
 * 天气javabean
 *
 * @author lyhuang
 * @date 2016-1-16 下午3:54:43
 */
public class WeatherBo {
	private String city;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 星期
	 */
	private String week;
	/**
	 * 当前温度
	 */
	private String curTemp;
	/**
	 * pm值
	 */
	private String aqi;
	/**
	 * 风向
	 */
	private String fengxiang;
	/**
	 * 风力
	 */
	private String fengli;
	/**
	 * 最高温度
	 */
	private String hightemp;
	/**
	 * 最低温度
	 */
	private String lowtemp;
	/**
	 * 天气状态   
	 */
	private String type;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getCurTemp() {
		return curTemp;
	}
	public void setCurTemp(String curTemp) {
		this.curTemp = curTemp;
	}
	public String getAqi() {
		return aqi;
	}
	public void setAqi(String aqi) {
		this.aqi = aqi;
	}
	public String getFengxiang() {
		return fengxiang;
	}
	public void setFengxiang(String fengxiang) {
		this.fengxiang = fengxiang;
	}
	public String getFengli() {
		return fengli;
	}
	public void setFengli(String fengli) {
		this.fengli = fengli;
	}
	public String getHightemp() {
		return hightemp;
	}
	public void setHightemp(String hightemp) {
		this.hightemp = hightemp;
	}
	public String getLowtemp() {
		return lowtemp;
	}
	public void setLowtemp(String lowtemp) {
		this.lowtemp = lowtemp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@Override
	public String toString() {
		return "WeatherBo [city=" + city + ", date=" + date + ", week=" + week
				+ ", curTemp=" + curTemp + ", aqi=" + aqi + ", fengxiang="
				+ fengxiang + ", fengli=" + fengli + ", hightemp=" + hightemp
				+ ", lowtemp=" + lowtemp + ", type=" + type + "]";
	}
	
	
}
