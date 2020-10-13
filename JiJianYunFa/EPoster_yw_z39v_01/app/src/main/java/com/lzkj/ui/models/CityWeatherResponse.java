package com.lzkj.ui.models;

import java.util.List;

/**
 *城市天气回复类
 *
 * @author lyhuang
 * @date 2016-1-16 下午3:53:30
 */
public class CityWeatherResponse extends WeatherBaseResponse {
	private CityWeatherBo retData;

	public CityWeatherBo getRetData() {
		return retData;
	}

	public void setRetData(CityWeatherBo retData) {
		this.retData = retData;
	}

	@Override
	public String toString() {
		return "CityInfoResponse [retData=" + retData + ", errNum=" + errNum
				+ ", retMsg=" + retMsg + "]";
	}
	
}
