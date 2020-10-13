package com.lzkj.downloadservice.util;

import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.interfaces.IRequestCallback;
import com.lzkj.downloadservice.log.LogManager;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

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
    //心跳服务器地址
//	private String heartServerAddress = SharedUtil.newInstance().getString(SharedUtil.HEARTSERVER_ID);
//	private int retry = 5;
//	private int index = 0;
//	private HttpURLConnection httpURLConnection;
    /**消息推送回执的url*/
//	private static  String PUSH_RECEIPT_URL = "http://%s/adpress-message/report/device/receiptReport.do";
    /**
     * 汇报下载计划的url
     */
    private static String DOWNLOAD_PLAN_RECEIPT_URL = "http://%s/adpress-message/report/device/receiptReport.do";

    /**
     * 汇报终端流量使用情况的url
     */
    private static String URL_REPORTS_TRAFFIC = "http://%s//mallappss-message/reports/%s/traffics";

    /**
     * 消息推送回执的url
     */
    public static String PUSH_RECEIPT_URL = "http://%s/mallappss-message/commands/receipts";

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
        executorService = Executors.newSingleThreadExecutor();
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
     * @param httpRequest 要发送的数据
     */
    public void sendGetRequest(HttpRequestBean httpRequest) {
        if (checkIsNull(httpRequest)) {
            return;
        }
        startThreadRequest(httpRequest, "GET");
    }

    /**
     * 发送请求
     *
     * @param httpRequest
     * @param requestMethodName
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
                IRequestCallback requestCallback = httpRequestBean.getRequestCallback();
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
                                LogUtils.d(LOG_TAG, "postRequest.run", "url:" + httpRequestBean.getRequestUrl() + "?" + httpRequestBean.getRequestParm());
                            }
                        } else {
                            httpURL = new URL(httpRequestBean.getRequestUrl());
                        }
                        LogManager.get().insertOperationMessage(httpRequestBean.getRequestUrl()
                                + "?" + httpRequestBean.getRequestParm()
                                + "_" + Helper.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss") + "\r\n");

                        LogUtils.d(LOG_TAG, "postRequest", "url:" + httpRequestBean.getRequestUrl() + "?" + httpRequestBean.getRequestParm());
                        HttpURLConnection httpURLConnection = (HttpURLConnection) httpURL.openConnection();
                        httpURLConnection.setRequestMethod(requestMethodName);
                        httpURLConnection.setReadTimeout(Constant.CONNECT_TIME_OUT);
                        httpURLConnection.setConnectTimeout(Constant.CONNECT_TIME_OUT);
                        httpURLConnection.setRequestProperty("Charset", "UTF-8");
                        if ("POST".equals(requestMethodName)) {
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setRequestProperty("Connection", "keep-alive");
                            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//							httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");  
                            if (null != httpRequestBean.getRequestParm()) {
                                writerDataToServer(httpURLConnection.getOutputStream(), httpRequestBean.getRequestParm());
                            }
                        }
                        LogUtils.d(LOG_TAG, "postRequest", "getResponseCode: " + httpURLConnection.getResponseCode()); // 200
                        if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                            httpMap.put(tag, httpURLConnection);
                            if ("HeartService".equals(tag)) {
                                requestCallback.onSuccess(null, httpRequest.getRequestUrl(), httpRequest.getRequestTag());
                            } else {
                                String result = getResponseValue(httpURLConnection.getInputStream());
                                if (null != requestCallback) {
                                    if (null != result) {
                                        requestCallback.onSuccess(result,
                                                httpRequest.getRequestUrl(), httpRequest.getRequestTag());
                                    } else {
                                        requestCallback.onFaile("result is null!",
                                                httpRequest.getRequestUrl(), httpRequest.getRequestTag());
                                    }
                                }
                            }
                            httpMap.remove(tag);
                        }
                        index = retry;
                        return;
                    } catch (IOException e) {
                        try {
                            index++;
                            Thread.sleep(Constant.RETRY_SLEEP_TIME);
                            errMsg = e.getMessage();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        LogUtils.d(LOG_TAG, "sendGetRequest.run", "index:" + index);
                        e.printStackTrace();
                    }
                }
                LogUtils.d(LOG_TAG, "sendGetRequest.run", "errMsg:" + errMsg);
                if (null != errMsg) {
                    removeCancelList(tag);
                    if (null != requestCallback) {
                        requestCallback.onFaile(errMsg,
                                httpRequest.getRequestUrl(), httpRequest.getRequestTag());
                    }
                }
            }
        });
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
        startThreadRequest(httpRequestBean, "POST");
    }

    /**
     * 发送数据到服务器
     *
     * @param outputStream
     * @throws IOException
     */
    private void writerDataToServer(OutputStream outputStream, String jsonData) throws IOException {
        LogUtils.d(LOG_TAG, "writerDataToServer", "jsonData:" + jsonData);
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
     * 根据http地址获取文件的大小
     *
     * @param httpUrl
     * @return
     */
    public int getContentLength(String httpUrl) {
        try {
            URL url = new URL(httpUrl);
//				URL url = new URL(FileUtil.encodeUrl(httpUrl));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.connect();
            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()
                    || HttpURLConnection.HTTP_PARTIAL == httpURLConnection.getResponseCode()) {
                return httpURLConnection.getContentLength();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

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
     * 获取回执服务器地址
     *
     * @return
     */
    public static String getReportServer() {
//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : CommutShardUtil.newInstance().getString(CommutShardUtil.MESSAGE_SERVER_ID);

        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "112.74.12.254:8080"; // 正式服务器

//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "10.10.13.150:8080"; // 测试服务器

//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "120.79.219.145:8080"; // 极简测试服务器

        return String.format(PUSH_RECEIPT_URL, server.split(";")[0]);
    }

    /**
     * 获取下载计划回执服务器地址
     *
     * @return
     */
    public static String getDownloadPlanReportServer() {
//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : CommutShardUtil.newInstance().getString(CommutShardUtil.MESSAGE_SERVER_ID);

        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "112.74.12.254:8080"; // 正式服务器

//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "10.10.13.150:8080"; // 测试服务器

//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "120.79.219.145:8080"; // 极简测试服务器

        return String.format(DOWNLOAD_PLAN_RECEIPT_URL, server.split(";")[0]);
    }

    /**
     * @return 获取汇报终端流量的服务器地址
     */
    public static String getReportTrafficUrl() {
//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : CommutShardUtil.newInstance().getString(CommutShardUtil.MESSAGE_SERVER_ID);

        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "112.74.12.254:8080"; // 正式服务器

//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "10.10.13.150:8080"; // 测试服务器

//        String server = ConfigSettings.IFTEST ? Constant.TEST_SERVER : "120.79.219.145:8080"; // 极简测试服务器

        return String.format(URL_REPORTS_TRAFFIC, server, ConfigSettings.getDeviceId());
    }

    private boolean checkIsNull(HttpRequestBean httpRequestBean) {
        if (isNull(httpRequestBean.getRequestUrl()) || isNull(httpRequestBean.getRequestTag())) {
            if (null != httpRequestBean.getRequestCallback()) {
                httpRequestBean.getRequestCallback().onFaile("requestUrl is null or tag is null",
                        httpRequestBean.getRequestUrl(), httpRequestBean.getRequestTag());
            }
            return true;
        }
        removeCancelList(httpRequestBean.getRequestTag());
        return false;
    }

    private boolean isNull(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 停止线程池
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
