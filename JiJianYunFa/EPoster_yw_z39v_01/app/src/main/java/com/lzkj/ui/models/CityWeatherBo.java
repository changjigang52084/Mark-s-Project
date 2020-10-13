package com.lzkj.ui.models;

import java.util.List;

/**
 * 城市天气信息（包含未来和历史天气）
 *
 * @author lyhuang
 * @date 2016-1-16 下午4:06:33
 */
public class CityWeatherBo {
	/**
	 * 城市名
	 */
	private String city;
	/**
	 * 城市代码
	 */
	private String cityid;
	/**
	 * 今日天气信息
	 */
	private WeatherBo today;
	
	/**
	 * 未来预报列表
	 */
	private List<WeatherBo> forecast;
	
	/**
	 * 历史
	 */
	private List<WeatherBo> history;


	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public WeatherBo getToday() {
		return today;
	}

	public void setToday(WeatherBo today) {
		this.today = today;
	}

	public List<WeatherBo> getForecast() {
		return forecast;
	}

	public void setForecast(List<WeatherBo> forecast) {
		this.forecast = forecast;
	}

	public List<WeatherBo> getHistory() {
		return history;
	}

	public void setHistory(List<WeatherBo> history) {
		this.history = history;
	}

	@Override
	public String toString() {
		return "CityWeatheResponse [cityName=" + city + ", cityCode="
				+ cityid + ", today=" + today + ", forecast=" + forecast
				+ ", history=" + history  + "]";
	}
	
}
