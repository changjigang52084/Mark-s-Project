package com.sunchip.adw.cloudphotoframe.interfaces;

public    interface ResponseCallback {

    void onResponse(String string);
    void onError(int code,String ErrarString,Throwable throwable);
    void onException(String s);
    void UploadProBar();
}
