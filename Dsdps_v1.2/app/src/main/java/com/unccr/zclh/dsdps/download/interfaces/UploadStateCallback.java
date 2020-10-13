package com.unccr.zclh.dsdps.download.interfaces;

public interface UploadStateCallback {

    /**下载成功*/
    void onUploadSuccess(String uploadLocleFile);
    /**下载失败，失败的原因*/
    void onUploadFail(String uploadLocleFile, String errMsg);
    /**下载进度条*/
    void onUploadUpdateProgreass(int progress, long totalSize, String uploadLocleFile);
}
