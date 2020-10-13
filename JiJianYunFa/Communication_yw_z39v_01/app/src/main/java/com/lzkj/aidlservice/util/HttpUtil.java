package com.lzkj.aidlservice.util;

import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.api.heart.HeartService;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.manager.HttpManager;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.log.LogManager;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发送http请求的工具类
 *
 * @author changkai
 */
public class HttpUtil {
    private static final LogTag LOG_TAG = LogUtils.getLogTag(HttpUtil.class.getSimpleName(), true);
    private static volatile HttpUtil httpUitl = null;
    /**
     * 保存连接
     */
    private static Map<String, HttpURLConnection> httpMap = new HashMap<String, HttpURLConnection>();
    /**
     * 取消连接
     */
    private static CopyOnWriteArrayList<String> cancelList = new CopyOnWriteArrayList<String>();
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
        HttpManager.getInstance().httpGet(httpRequest);
    }

    /**
     * 启动线程发送http请求
     *
     * @param httpRequest       要发送请求的参数bean
     * @param requestMethodName 请求的方法(GET or POST)
     */
    private void startThreadRequest(final HttpRequestBean httpRequest, final String requestMethodName) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpRequestBean httpRequestBean = httpRequest;
                int retry = httpRequestBean.getRequestRestry() == 0 ? 5 : httpRequestBean.getRequestRestry();
                int index = 0;
                String errMsg = null;
                String tag = httpRequestBean.getRequestTag();
                IRequestCallback iRequestCallback = httpRequestBean.getRequestCallback();
                while (index < retry) {
                    try {
                        if (cancelList.contains(tag)) {
                            LogUtils.d(LOG_TAG, "postRequest.run", "tag：" + tag + "contains(tag)");
                            index = retry;
                            continue;
                        }
                        URL httpURL = null;
                        if ("GET".equals(requestMethodName)) {
                            if (null == httpRequestBean.getRequestParm()) {
                                httpURL = new URL(httpRequestBean.getRequestUrl());
                            } else {
                                httpURL = new URL(httpRequestBean.getRequestUrl() + "?" + httpRequestBean.getRequestParm());
                            }
                        } else {
                            httpURL = new URL(httpRequestBean.getRequestUrl());
                        }
                        LogUtils.d(LOG_TAG, "startThreadRequest", "url:" + httpRequestBean.getRequestUrl() + "?" + httpRequestBean.getRequestParm());

                        LogManager.get().insertOperationMessage(httpRequestBean.getRequestUrl()
                                + "?" + httpRequestBean.getRequestParm()
                                + "_" + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss") + "\r\n");

                        HttpURLConnection httpURLConnection = (HttpURLConnection) httpURL.openConnection();
                        httpURLConnection.setRequestMethod(requestMethodName);
                        httpURLConnection.setReadTimeout(Constant.CONNECT_TIME_OUT);
                        httpURLConnection.setConnectTimeout(Constant.CONNECT_TIME_OUT);
                        httpURLConnection.setRequestProperty("Charset", "UTF-8");
                        if ("POST".equals(requestMethodName)) {
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setRequestProperty("Connection", "keep-alive");
                            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            if (null != httpRequestBean.getRequestParm()) {
                                writerDataToServer(httpURLConnection.getOutputStream(), httpRequestBean.getRequestParm());
                            }
                        }
                        LogUtils.d(LOG_TAG, "postRequest.run", "ResponseCode=" + httpURLConnection.getResponseCode());
                        if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                            httpMap.put(tag, httpURLConnection);
                            if (HeartService.class.getSimpleName().equals(tag)) {
                                iRequestCallback.onSuccess(null, tag, httpRequestBean.getRequestUrl());
                            } else {
                                String result = getResponseValue(httpURLConnection.getInputStream());
                                if (null != iRequestCallback) {
                                    iRequestCallback.onSuccess(result, tag, httpRequestBean.getRequestUrl());
                                }
                            }
                            httpMap.remove(tag);
                        } else {
                            handlerErr("errCode: " + httpURLConnection.getResponseCode(),
                                    tag, iRequestCallback, httpRequestBean.getRequestUrl());
                        }
                        index = retry;
                        return;
                    } catch (IOException e) {
                        try {
                            index++;
                            Thread.sleep(Constant.RETRY_SLEEP_TIME);
                            errMsg = StringUtil.getString(R.string.connect_timeout);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        LogUtils.d(LOG_TAG, "sendGetRequest.run", "index: " + index);
                        e.printStackTrace();
                    }
                }
                LogUtils.d(LOG_TAG, "sendGetRequest.run", "errMsg: " + errMsg);
                handlerErr(errMsg, tag, iRequestCallback, httpRequestBean.getRequestUrl());
            }
        });
    }

    /**
     * 处理错误信息
     *
     * @param errMsg           错误信息
     * @param tag              标志
     * @param iRequestCallback 接口回调
     */
    private void handlerErr(String errMsg, String tag, IRequestCallback iRequestCallback, String requestUrl) {
        if (null != errMsg) {
            removeCancelList(tag);
            if (null != iRequestCallback) {
                iRequestCallback.onFaile(errMsg, tag, requestUrl);
            }
        } else {
            if (null != iRequestCallback) {
                iRequestCallback.onFaile(errMsg, tag, requestUrl);
            }
        }
    }

    /***
     * 发送post请求
     * @param httpRequestBean
     * 				请求的参数
     */
    public void postRequest(final HttpRequestBean httpRequestBean) {
        if (checkIsNull(httpRequestBean)) {
            return;
        }
        HttpManager.getInstance().httpPost(httpRequestBean);
    }

    /**
     * 发送数据到服务器
     *
     * @param outputStream
     * @throws IOException
     */
    private void writerDataToServer(OutputStream outputStream, String jsonData) throws IOException {
//		LogUtils.d(LOG_TAG,"writerDataToServer","jsonData:"+jsonData);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(jsonData);
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStream.close();
    }

    /**
     * 读取返回结果
     *
     * @param inputStream
     * @return 请求的返回值
     * @throws IOException
     */
    private String getResponseValue(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String temp = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp);
        }
        bufferedReader.close();
        inputStream.close();
        return stringBuilder.toString();
    }

    /**
     * 取消连接
     *
     * @param tag
     */
    public void cancleRequestToTag(String tag) {
        try {
            cancelList.add(tag);
            httpMap.get(tag).disconnect();
            httpMap.remove(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除掉取消连接里面保存的tag
     *
     * @param tag
     */
    private void removeCancelList(String tag) {
        if (cancelList.contains(tag)) {
            cancelList.remove(tag);
        }
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
        removeCancelList(httpRequestBean.getRequestTag());
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

    /**
     * 停止线程池运行
     */
    public void shutdown() {
        if (null != executorService) {
            LogUtils.d(LOG_TAG, "shutdown()", "executorService shudown");
            executorService.shutdown();
        }
    }
}
