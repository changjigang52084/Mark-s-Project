package com.lzkj.aidlservice.api.impl;

import android.util.Log;

import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.manager.HttpManager;
import com.lzkj.aidlservice.bo.HttpBo;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.StringUtil;

import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月19日 上午11:43:08
 * @parameter 重新发送回执的类
 */
public class RetryReportMsgImpl implements IRequestCallback {

    private static final String TAG = "RetryReportMsgImpl";

    /**
     * 重新发送回执
     */
    public void retrySendReport() {
        Log.d(TAG,"RetryReportMsgImpl retrySendReport...");
        List<HttpBo> httpBoList = SQLiterManager.getInstance().getHttpBoList();
        if (null != httpBoList && !httpBoList.isEmpty()) {
            int size = httpBoList.size();
            for (int i = 0; i < size; i++) {
                HttpBo httpBo = httpBoList.get(i);
                String httpTag = httpBo.getHttpRequestTag();
                Log.d(TAG, "RetryReportMsgImpl retrySendReport httpTag: " + httpTag);
                if (!StringUtil.isNullStr(httpTag) && httpTag.equals("responseReceiver")) {
//                    SQLiterManager.getInstance().delHttpBo(httpBo.getHttpUrl(), httpTag);
                    continue;
                }
                HttpRequestBean requestBean = new HttpRequestBean();
                requestBean.setRequestTag(httpBo.getHttpRequestTag());
                requestBean.setRequestUrl(httpBo.getHttpUrl());
                requestBean.setRequestCallback(this);
                requestBean.setRequestParm(httpBo.getHttpRequestParam());
                if (null != httpBo.getHttpMethod()) {
                    if (httpBo.getHttpMethod().equals(HttpManager.POST)) {
                        HttpUtil.newInstance().postRequest(requestBean);
                    } else {
                        HttpUtil.newInstance().sendGetRequest(requestBean);
                    }
                } else {
                    HttpUtil.newInstance().postRequest(requestBean);
                }
            }
        }
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        Log.d(TAG, "RetryReportMsgImpl onSuccess result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        //SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        Log.d(TAG, "RetryReportMsgImpl onFaile errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
    }
}
