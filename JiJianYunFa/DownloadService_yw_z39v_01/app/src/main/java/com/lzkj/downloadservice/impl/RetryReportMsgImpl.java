package com.lzkj.downloadservice.impl;

import com.lzkj.downloadservice.bean.HttpBo;
import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.interfaces.IRequestCallback;
import com.lzkj.downloadservice.util.HttpUtil;

import java.util.List;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/10/18.
 * @Parameter 重新发送未成功的回执
 */

public class RetryReportMsgImpl implements Runnable, IRequestCallback {

    @Override
    public void run() {
        retrySendReport();
    }

    /**
     * 重新发送回执
     */
    private void retrySendReport() {
        List<HttpBo> httpBoList = SQLiteManager.getInstance().getHttpBoList();
        if (null != httpBoList && !httpBoList.isEmpty()) {
            int size = httpBoList.size();
            for (int i = 0; i < size; i++) {
                HttpBo httpBo = httpBoList.get(i);
                HttpRequestBean requestBean = new HttpRequestBean();
                requestBean.setRequestTag(httpBo.getHttpRequestTag());
                requestBean.setRequestUrl(httpBo.getHttpUrl());
                requestBean.setRequestCallback(this);
                requestBean.setRequestParm(httpBo.getHttpRequestParam());
                HttpUtil.newInstance().postRequest(requestBean);
            }
        }
    }

    @Override
    public void onSuccess(String result, String httpUrl, String requestTag) {
        SQLiteManager.getInstance().delHttpBo(httpUrl, requestTag);
    }

    @Override
    public void onFaile(String errMsg, String httpUrl, String requestTag) {
    }
}
