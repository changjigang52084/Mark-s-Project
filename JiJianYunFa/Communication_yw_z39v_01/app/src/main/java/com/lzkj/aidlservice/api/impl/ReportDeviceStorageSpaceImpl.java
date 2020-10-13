package com.lzkj.aidlservice.api.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.AdpressStorageReportPackage;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.sync.RequestSyncDeviceInfo;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.DiskSpaceUtil;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/10/19.
 * @Parameter 汇报终端磁盘的类
 */

public class ReportDeviceStorageSpaceImpl implements IRequestCallback {
    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(ReportDeviceStorageSpaceImpl.class.getSimpleName(), true);

    private final static long SIZE_KB = 1024L;
    private final static long SIZE_MB = SIZE_KB * SIZE_KB;

    /**
     * 存储空间变化差值10M，用以判断是否汇报磁盘空间。
     */
    private static final long STORAGE_SPACE_DIFF_VALUE = 10 * SIZE_MB;
    /**
     * 本地磁盘可用空间
     */
    private long availableDiskSpace = 0L;


    /**
     * 检测存储空间，如果可用空间量变化超过指定值，返回true.
     * @return boolean 是否汇报
     */
//    private boolean checkStorageSpace(){
//        long currentAvailableDiskSpace = (long)DiskSpaceUtil.getAvailableSpaceKB();
//        long diff = Math.abs(currentAvailableDiskSpace - availableDiskSpace) * SIZE_KB ;
//        this.availableDiskSpace = currentAvailableDiskSpace;
//        LogUtils.d(TAG, "checkStorageSpace", "Storage sapce diff value:" + diff);
//        return diff > STORAGE_SPACE_DIFF_VALUE;
//    }

    /**
     * 汇报磁盘存储空间
     */
    public void reportDeviceStorageSpace() {
        LogUtils.d(TAG, "reportStorageSpace", "Report storage space.");
        String requestUrl = HttpConfigSetting.getReportDeviceStrogeUrl(ConfigSettings.getDid());
        if (TextUtils.isEmpty(requestUrl)) {
            LogUtils.e(TAG, "reportDeviceState", "RequestUrl is null.");
            return;
        }

        String requestParam = getRequestParam();
        if (TextUtils.isEmpty(requestParam)) {
            LogUtils.e(TAG, "reportDeviceState", "requestParam is null.");
            return;
        }
        LogUtils.e(TAG, "reportDeviceStorageSpace", "requestParam: " + requestParam);
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestParm(requestParam);
        httpRequestBean.setRequestUrl(requestUrl);
        httpRequestBean.setRequestCallback(this);
        httpRequestBean.setRequestTag(RequestSyncDeviceInfo.class.getSimpleName());
        HttpUtil.newInstance().postRequest(httpRequestBean);
    }

    /**
     * 汇报TF卡存储空间
     */
    public void reportTFStorageSpace() {
        LogUtils.d(TAG, "reportTFStorageSpace", "Report storage space.");
        String requestUrl = HttpConfigSetting.getReportDeviceTFUrl(ConfigSettings.getDid());
        if (TextUtils.isEmpty(requestUrl)) {
            LogUtils.e(TAG, "reportTFStorageSpace", "RequestUrl is null.");
            return;
        }

        String requestTFParam = getTFRequestParam();
        if (TextUtils.isEmpty(requestTFParam)) {
            LogUtils.e(TAG, "reportTFStorageSpace", "RequestParm is null.");
            return;
        }
        LogUtils.e(TAG, "reportTFStorageSpace", "requestTFParam: " + requestTFParam);
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestParm(requestTFParam);
        httpRequestBean.setRequestUrl(requestUrl);
        httpRequestBean.setRequestCallback(this);
        httpRequestBean.setRequestTag(RequestSyncDeviceInfo.class.getSimpleName());
        HttpUtil.newInstance().postRequest(httpRequestBean);
    }

    /**
     * 获取要发送的数据
     *
     * @return
     */
    private String getRequestParam() {
        AdpressStorageReportPackage storageReportPackage = new AdpressStorageReportPackage();
        storageReportPackage.setAvaliable((long) DiskSpaceUtil.getAvailableSpaceKB());
        storageReportPackage.setTotal((long) DiskSpaceUtil.getTotalSpaceKB());
        return JSON.toJSONString(storageReportPackage);
    }

    /**
     * 获取要发送的数据
     *
     * @return
     */
    private String getTFRequestParam() {
        AdpressStorageReportPackage storageReportPackage = new AdpressStorageReportPackage();
        storageReportPackage.setAvaliable((long) DiskSpaceUtil.getAvailableTFSpaceKB());
        storageReportPackage.setTotal((long) DiskSpaceUtil.getTotalTFSpaceKB());
        return JSON.toJSONString(storageReportPackage);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        LogUtils.d(TAG, "onSuccess", "result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
//        SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        LogUtils.e(TAG, "onFaile", "errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
    }
}
