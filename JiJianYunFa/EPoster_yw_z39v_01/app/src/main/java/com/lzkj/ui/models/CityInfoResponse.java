package com.lzkj.ui.models;

/**
 * 城市信息回复类
 *
 * @author lyhuang
 * @date 2016-1-16 下午3:53:08
 */
public class CityInfoResponse extends WeatherBaseResponse{
	private CityInfoBo retData;

	public CityInfoBo getRetData() {
		return retData;
	}

	public void setRetData(CityInfoBo retData) {
		this.retData = retData;
	}

	@Override
	public String toString() {
		return "CityInfoResponse [retData=" + retData + ", errNum=" + errNum
				+ ", retMsg=" + retMsg + "]";
	}
	
	
}
