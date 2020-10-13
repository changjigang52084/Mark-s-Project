package com.xunixianshi.vrshow.interfaces;


public class EventBusInterface {
	private String mMsg;
	private int mCode;

	public EventBusInterface(String msg) {
		this.mMsg = msg;
	}

	public EventBusInterface(int code) {
		this.mCode = code;
	}


	public String getMsg() {
		return mMsg;
	}

	public int getCode() {
		return mCode;
	}

}
