package com.lzkj.aidlservice.bo;
/**
 * 注册java bean
 * @author changkai
 *
 */
public class RegistrRequst {
	private RegistrBeanRequst req;
	private int type;
	public RegistrBeanRequst getReq() {
		return req;
	}
	public void setReq(RegistrBeanRequst req) {
		this.req = req;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
