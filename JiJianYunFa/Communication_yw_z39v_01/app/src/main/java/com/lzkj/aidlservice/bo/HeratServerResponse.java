package com.lzkj.aidlservice.bo;
/**
 * 心跳服务器返回以后的结果
 * @author changkai
 *
 */
public class HeratServerResponse {
	private HeratServerBeanResponse rsp;
	private int type;
	public HeratServerResponse() {}
	public HeratServerBeanResponse getRsp() {
		return rsp;
	}
	public void setRsp(HeratServerBeanResponse rsp) {
		this.rsp = rsp;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
