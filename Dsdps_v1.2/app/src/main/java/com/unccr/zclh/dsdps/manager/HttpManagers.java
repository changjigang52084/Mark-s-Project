package com.unccr.zclh.dsdps.manager;

import android.os.Environment;
import android.util.Log;

import com.unccr.zclh.dsdps.download.bean.HttpRequestBean;
import com.unccr.zclh.dsdps.download.interfaces.IRequestCallback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpManagers {

    private static final String TAG = "HttpManager";

    /**
     * 上传图片的key
     */
    private static final String ACCESS_KEY = "accessKey";
    /**
     * 上传图片的value
     */
    private static final String ACCESS_VALUE = "CdsqByl";

    public static final String POST =  "POST";


    private static volatile HttpManagers mHttpManager;

    private OkHttpClient mOkHttpClient = null;

    private HttpManagers() {
        initOkHttp();
    }

    public static HttpManagers getInstance() {
        if (null == mHttpManager) {
            synchronized (HttpManagers.class) {
                if (null == mHttpManager) {
                    mHttpManager = new HttpManagers();
                }
            }
        }
        return mHttpManager;
    }

    private void initOkHttp() {
        File cacheFile = Environment.getDownloadCacheDirectory();
        int cacheSize = 50 * 1024 * 1024;
        long timeout = 30L;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(new Cache(cacheFile, cacheSize));
        mOkHttpClient = builder.build();
    }

    /**
     * 发送Get请求 每五秒请求一次
     * @param httpRequestBean
     */
    public void httpGet(final HttpRequestBean httpRequestBean) {
        Log.d(TAG, "httpGet: " + httpRequestBean.getRequestUrl() + "?" +httpRequestBean.getRequestParm());

        if (null != httpRequestBean) {
            String httpURL;
            if (null == httpRequestBean.getRequestParm()) {
                httpURL = httpRequestBean.getRequestUrl();
            } else {
                httpURL = httpRequestBean.getRequestUrl() + "?" + httpRequestBean.getRequestParm();
            }
            Request.Builder requestBuilder = new Request.Builder().url(httpURL);

            mOkHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                    if (null != iRequestCallback) {
                        iRequestCallback.onFaile(e.getMessage(), httpRequestBean.getRequestTag(), httpRequestBean.getRequestUrl());
                    }
                    Log.d(TAG, "httpGet onFailure msgError: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d(TAG, "httpGet onResponse result: " + result);
                    IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                    if (null != iRequestCallback && response.isSuccessful()) {
                        iRequestCallback.onSuccess(result, httpRequestBean.getRequestTag(), httpRequestBean.getRequestUrl());
                    }
                }
            });
        }
    }
}
