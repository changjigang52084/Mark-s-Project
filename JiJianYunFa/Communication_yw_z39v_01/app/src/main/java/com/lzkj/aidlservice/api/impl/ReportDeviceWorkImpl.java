package com.lzkj.aidlservice.api.impl;

import android.text.TextUtils;

import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.manager.HttpManager;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/10/18.
 * @Parameter 汇报终端工作信息的类
 */

public class ReportDeviceWorkImpl implements IRequestCallback {

    private String mReportMsg;
    /**
     * 是否汇报售货机的信息 true表示是
     **/
    private boolean isReportVendors = false;
    private static final LogUtils.LogTag LOG_TAG = LogUtils.getLogTag(ReportDeviceWorkImpl.class.getSimpleName(), true);

    public ReportDeviceWorkImpl(String reportMsg) {
        mReportMsg = reportMsg;
    }

    public void setReportMsg(String reportMsg) {
        mReportMsg = reportMsg;
    }

    public void setReportVendors(boolean reportVendors) {
        isReportVendors = reportVendors;
    }

    /**
     * 汇报信息
     */
    public void reportDeviceWorkMsg() {
        if (!TextUtils.isEmpty(mReportMsg)) {
            LogUtils.d(LOG_TAG, "reportDeviceWorkImpl", "reportDeviceWorkMsg mReportMsg: " + mReportMsg);
            HttpRequestBean httpRequestBean = new HttpRequestBean();
            httpRequestBean.setRequestTag(ReportDeviceWorkImpl.class.getSimpleName() + System.currentTimeMillis());
            httpRequestBean.setRequestParm(mReportMsg);
            httpRequestBean.setRequestUrl(getReportDeviceWorkRequestUrl());
            httpRequestBean.setRequestCallback(this);
            HttpUtil.newInstance().postRequest(httpRequestBean);
            //SQLiterManager.getInstance().insterHttpBo(httpRequestBean, HttpManager.POST);
        }
    }

    private String getReportDeviceWorkRequestUrl() {
        String messageServer = HttpConfigSetting.getMessageServer();
        return String.format(isReportVendors ? HttpConstants.REPORT_VENDORS_WORK_URL : HttpConstants.REPORT_DEVICE_WORK_URL, messageServer, ConfigSettings.getDid());
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        LogUtils.d(LOG_TAG, "reportDeviceWorkImpl onSuccess", " ,result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
//        SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        LogUtils.w(LOG_TAG, "reportDeviceWorkImpl onFaile", " ,errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
    }
}
