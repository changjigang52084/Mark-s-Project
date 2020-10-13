package com.yqhd.baizelocationlib.entity;

/**
  * @ClassName: CityBo
  * @Description: TODO 城市Javabean
  * @author longyihuang
  * @date 2016年9月24日 上午10:51:32
  *
  */
public class CityBo {
	private String cityName;
	private Integer proId;
	private Integer cityId;
	
	
	public CityBo() {
	}


	public CityBo(String cityName, Integer proId, Integer cityId) {
		super();
		this.cityName = cityName;
		this.proId = proId;
		this.cityId = cityId;
	}


	public String getCityName() {
		return cityName;
	}


	public void setCityName(String cityName) {
		this.cityName = cityName;
	}


	public Integer getProId() {
		return proId;
	}


	public void setProId(Integer proId) {
		this.proId = proId;
	}


	public Integer getCityId() {
		return cityId;
	}


	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}


	@Override
	public String toString() {
		return "CityBo [cityName=" + cityName + ", proId=" + proId + ", cityId=" + cityId + "]";
	}
	
	
}
