package com.lzkj.aidlservice.api.sync;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.SyncDeviceApplicationVersionPackage;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.StringUtil;


/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/11/23.
 * @Parameter  发送同步app版本的线程
 */

public class RequestSyncAppVersionRunnable implements Runnable,IRequestCallback {
    private static final LogUtils.LogTag LOG_TAG = LogUtils.getLogTag(RequestSyncAppVersionRunnable.class, true);

    public RequestSyncAppVersionRunnable(String method) {
        LogUtils.d(LOG_TAG, "RequestSyncAppVersionRunnable", "method :" + method);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        LogUtils.d(LOG_TAG, "onSuccess", "result: " + result);
//        SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        LogUtils.d(LOG_TAG, "onFaile", "errMsg :" + errMsg);
    }

    @Override
    public void run() {
        if (!StringUtil.isNullStr(ConfigSettings.getDid())) {
            SyncDeviceApplicationVersionPackage appVersionDto = new SyncDeviceApplicationVersionPackage();
            appVersionDto.setDeviceId(Long.parseLong(ConfigSettings.getDid()));

            HttpRequestBean httpRequestBean = new HttpRequestBean();
            httpRequestBean.setRequestCallback(this);
            httpRequestBean.setRequestUrl(HttpConfigSetting.getSyncAppVersionUrl());
            httpRequestBean.setRequestParm(JSON.toJSONString(appVersionDto));
            httpRequestBean.setRequestTag(RequestSyncAppVersionRunnable.class.getSimpleName());

            HttpUtil.newInstance().postRequest(httpRequestBean);
        }
    }
}
