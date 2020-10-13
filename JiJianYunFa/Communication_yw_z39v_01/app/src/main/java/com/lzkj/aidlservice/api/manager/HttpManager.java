package com.lzkj.aidlservice.api.manager;

import android.os.Environment;
import android.util.Log;

import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.log.LogManager;
import com.lzkj.aidlservice.util.DateTimeUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/11/3.
 * @Parameter http请求管理类
 */

public class HttpManager {
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


    private static volatile HttpManager mHttpManager;

    private OkHttpClient mOkHttpClient = null;

    private HttpManager() {
        initOkHttp();
    }

    public static HttpManager getInstance() {
        if (null == mHttpManager) {
            synchronized (HttpManager.class) {
                if (null == mHttpManager) {
                    mHttpManager = new HttpManager();
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

            LogManager.get().insertOperationMessage(httpRequestBean.getRequestUrl() + "?" +httpRequestBean.getRequestParm() + "_" + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss") + "\r\n");

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

    /**
     * 发送post请求
     * @param httpRequestBean
     */
    public void httpPost(final HttpRequestBean httpRequestBean) {
        Log.d(TAG, "httpPost: " + httpRequestBean.getRequestUrl() + "?" +httpRequestBean.getRequestParm());

        if (null != httpRequestBean) {
            Request.Builder postBuilder = new Request.Builder();
            postBuilder.url(httpRequestBean.getRequestUrl());

            MediaType jsonMediaType = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, "");

            if (null != httpRequestBean.getRequestParm()) {
                requestBody = RequestBody.create(jsonMediaType,
                        httpRequestBean.getRequestParm());
            }
            postBuilder.post(requestBody)
                    .addHeader("Charset","UTF-8")
                    .addHeader("Connection", "keep-alive");
            LogManager.get().insertOperationMessage(httpRequestBean.getRequestUrl()
                    + "?" + httpRequestBean.getRequestParm()
                    + "_" + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss") + "\r\n");

            Request requestBuilder = postBuilder.build();

            mOkHttpClient.newCall(requestBuilder).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                    if (null != iRequestCallback) {
                        iRequestCallback.onFaile(e.getMessage(), httpRequestBean.getRequestTag(),
                                        httpRequestBean.getRequestUrl());
                    }
                    Log.d(TAG, "httpPost onFailure requestTag: " + httpRequestBean.getRequestTag() + " ,msgError: " + e.getMessage());
                    if(!httpRequestBean.getRequestTag().equals("responseDownloadState")){
                        //SQLiterManager.getInstance().insterHttpBo(httpRequestBean, HttpManager.POST);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                    if (null != iRequestCallback && response.isSuccessful()) {
                        iRequestCallback.onSuccess(result,
                                        httpRequestBean.getRequestTag(),
                                        httpRequestBean.getRequestUrl());
                    }
                    Log.d(TAG, "httpPost onResponse result: " + result + " ,url: " + httpRequestBean.getRequestUrl());
                }
            });
        }
    }

    /**
     * 上传文件
     * @param uploadFile 文件路径
     * @param httpRequestBean 请求bo
     * @param iProgressListener 进度监听
     */
    public void uploadFile(File uploadFile, final HttpRequestBean httpRequestBean,
                           IProgressListener iProgressListener) {
        if (null == httpRequestBean || httpRequestBean.getRequestCallback() == null) {
            return;
        }
        if (null == uploadFile || !uploadFile.exists()) {
            httpRequestBean.getRequestCallback().onFaile("upload file is null or not exists",
                    httpRequestBean.getRequestTag(), httpRequestBean.getRequestUrl());
            return;
        }

        RequestBody uploadFileRequestBody = createUploadFileRequestBody(MultipartBody.FORM,
                uploadFile, iProgressListener);

        RequestBody uploadRequestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(ACCESS_KEY, ACCESS_VALUE)
                .addFormDataPart("file", uploadFile.getName(), uploadFileRequestBody)
                .build();

        Request uploadRequest = new Request.Builder()
                                    .url(httpRequestBean.getRequestUrl())
                                    .addHeader("Referer", "http://www.dsplug.com")//白名单
                                    .post(uploadRequestBody)
                                    .tag(httpRequestBean.getRequestTag())
                                    .build();

        mOkHttpClient.newCall(uploadRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "uploadFile onFailure error msg: " + e.getMessage());
                IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                if (null != iRequestCallback) {
                    iRequestCallback.onFaile(e.getMessage(),
                            call.request().tag().toString(), httpRequestBean.getRequestUrl());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                if (response.isSuccessful() && null != iRequestCallback) {
                    String result = response.body().string();
                    iRequestCallback.onSuccess(result,
                            httpRequestBean.getRequestTag(), httpRequestBean.getRequestUrl());
                }
                Log.d(TAG, "uploadFile onResponse response code: " + response.code());
                call.cancel();
            }
       });
    }

    /**
     *  创建自定义的RequestBody
     * @param mediaType 文件类型
     * @param file  文件路径
     * @param iProgressListener 进度监听
     * @return 返回自定义的RequestBody
     */
    private RequestBody createUploadFileRequestBody(final MediaType mediaType,
                                              final  File file,
                                              final IProgressListener iProgressListener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() throws IOException {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buffer = new Buffer();
                    long conentLength = contentLength();
                    long length = -1;
                    long bufferSize = 1024 * 1024;
                    long progress = 0;
                    while ((length = source.read(buffer, bufferSize)) != -1) {
                        sink.write(buffer, length);
                        progress += length;
                        if (null != iProgressListener) {
                            iProgressListener.onProgress(conentLength,
                                    progress, (progress == conentLength));
                        }
                    }

                    sink.flush();
//                    sink.close();
                    source.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 下载文件
     * @param downloadFileUrl 下载文件的url
     * @param saveFilePath  保存的路径
     * @param iProgressListener 下载进度
     */
    public void downloadFile(final String downloadFileUrl, final File saveFilePath,
                             final IProgressListener iProgressListener) {
        if (null == downloadFileUrl || null == saveFilePath) {
            if (null != iProgressListener) {
                iProgressListener.onFailure(downloadFileUrl,
                        "http url is null or save download file is null");
            }
            return;
        }
        Request downloadRequest = new Request.Builder().url(downloadFileUrl).build();
        Call downloadCall = mOkHttpClient.newCall(downloadRequest);
        downloadCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "downloadFile onFailure errMsg: " + e.getMessage());
                if (null != iProgressListener) {
                    iProgressListener.onFailure(downloadFileUrl, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    long contentLength = responseBody.contentLength();
                    DataInputStream dataInputStream = new DataInputStream(responseBody.byteStream());
                    long progress = 0;
                    int length = -1;
                    byte[] buffer = new byte[1024 * 1024];
                    if (!saveFilePath.exists()) {
                        saveFilePath.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(saveFilePath);
                    while ((length = dataInputStream.read(buffer)) != -1) {
                        progress += length;
                        fileOutputStream.write(buffer, 0, length);
                        if (null != iProgressListener) {
                            iProgressListener.onProgress(contentLength, progress,
                                    (progress == contentLength));
                        }
                    }

                    dataInputStream.close();

                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        });
    }

    public interface IProgressListener {
        /**
         * 上传,下载 进度的监听
         * @param contentLength 文件的大小
         * @param progress  进度
         * @param isDone 是否完成
         */
        void onProgress(long contentLength, long progress, boolean isDone);

        /**
         * 错误接口
         * @param requestUrl 请求的url
         * @param errorMsg 错误的信息
         */
        void onFailure(String requestUrl, String errorMsg);
    }

    public void httpPostToRequestBody(String httpUrl, Map<String, String> requestBodyMap) {

        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : requestBodyMap.entrySet()) {
            formBody.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBody.build();

        Request postRequest = new Request.Builder()
                .url(httpUrl)
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void cancelCall() {
        mOkHttpClient.dispatcher().cancelAll();
    }
}
