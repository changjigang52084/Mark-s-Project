package com.lzkj.ui.models;

/**
 * 天气回复基类
 *
 * @author lyhuang
 * @date 2016-1-16 下午3:31:10
 */
public class WeatherBaseResponse {
	/**
	 * 错误码
	 */
	protected int errNum;
	/**
	 * 回复提示
	 */
	protected String retMsg;
	public int getErrNum() {
		return errNum;
	}
	public void setErrNum(int errNum) {
		this.errNum = errNum;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	
}
