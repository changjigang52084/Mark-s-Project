package com.unccr.zclh.dsdps.download.bean;


import com.unccr.zclh.dsdps.download.interfaces.IRequestCallback;

public class HttpRequestBean {
    private String requestUrl;
    private String requestParm;
    private IRequestCallback requestCallback;
    private String requestTag;
    private int requestRestry = 5;
    public String getRequestUrl() {
        return requestUrl;
    }
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
    public String getRequestParm() {
        return requestParm;
    }
    public void setRequestParm(String requestParm) {
        this.requestParm = requestParm;
    }
    public IRequestCallback getRequestCallback() {
        return requestCallback;
    }
    public void setRequestCallback(IRequestCallback requestCallback) {
        this.requestCallback = requestCallback;
    }
    public String getRequestTag() {
        return requestTag;
    }
    public void setRequestTag(String requestTag) {
        this.requestTag = requestTag;
    }
    public int getRequestRestry() {
        return requestRestry;
    }
    public void setRequestRestry(int requestRestry) {
        this.requestRestry = requestRestry;
    }

}