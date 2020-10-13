package com.unccr.zclh.dsdps.download.interfaces;

public interface IRequestCallback {

    void onSuccess(String result, String httpUrl, String requestTag);
    void onFaile(String errMsg, String httpUrl, String requestTag);
}
