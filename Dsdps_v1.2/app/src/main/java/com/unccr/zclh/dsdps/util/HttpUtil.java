package com.unccr.zclh.dsdps.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.unccr.zclh.dsdps.download.bean.HttpRequestBean;
import com.unccr.zclh.dsdps.manager.HttpManagers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUtil {

    private static AsyncHttpClient Asyncclient = new AsyncHttpClient();
    private static SyncHttpClient Syncclient = new SyncHttpClient();
    private static volatile HttpUtil httpUitl = null;

    static {
        Asyncclient.setTimeout(60000); // 设置链接超时，如果不设置，默认为10s
        Syncclient.setTimeout(60000);
    }

    public static void setBaseUrl() {

    }

    private ExecutorService executorService = null;

    private HttpUtil() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public static HttpUtil newInstance() {
        if (null == httpUitl) {
            synchronized (HttpUtil.class) {
                if (null == httpUitl) {
                    httpUitl = new HttpUtil();
                }
            }
        }
        return httpUitl;
    }

    /***
     * 发送get请求获取返回值
     */
    public void sendGetRequest(HttpRequestBean httpRequest) {
        if (checkIsNull(httpRequest)) {
            return;
        }
        HttpManagers.getInstance().httpGet(httpRequest);
    }

    /**
     * 判断传入的参数是否为空
     *
     * @param httpRequestBean 发送请求的参数
     * @return true表示为空, false表示不为空
     */
    private boolean checkIsNull(HttpRequestBean httpRequestBean) {
        if (isNull(httpRequestBean.getRequestUrl()) || isNull(httpRequestBean.getRequestTag())) {
            if (null != httpRequestBean.getRequestCallback()) {
                httpRequestBean
                        .getRequestCallback()
                        .onFaile("requestUrl is null or tag is null",
                                httpRequestBean.getRequestTag(), httpRequestBean.getRequestUrl());
            }
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return true表示空, 反之不为空
     */
    private boolean isNull(String str) {
        return null == str || "".equals(str);
    }



    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Asyncclient.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Asyncclient.post(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void getJSON(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Asyncclient.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void postJSON(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Asyncclient.post(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void getSyncJSON(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Syncclient.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void postSyncJSON(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Syncclient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postLiftInfoJSON(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Asyncclient.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
//        return Constants.SERVER_URL + relativeUrl;
//        return Constants.SERVER_URL_HEAD + DsdpsApp.getIpAddress()+Constants.SERVER_URL_PORT + relativeUrl;
        return Constants.SERVER_URL_HEAD + SharedUtil.newInstance().getString("ipAddress",Constants.SERVER_URL_IP)+Constants.SERVER_URL_PORT + relativeUrl;
    }
}








