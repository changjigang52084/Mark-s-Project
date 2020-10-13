//package com.lzkj.ui.util;
//
//import android.util.Log;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
//import java.net.URL;
//
///**
// * Created by Administrator on 2018/1/30.
// */
//
//public class HttpUrl {
//
//    private static final String TAG = "HttpUrl";
//
//    /**
//     * 节目开始播放get的方式请求
//     *
//     * @return 返回null 登录异常
//     */
//    public static void playProgramByGet(final String urlPath) {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(urlPath);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setConnectTimeout(5000);
//                    connection.setRequestMethod("GET");
//                    //获得结果码
//                    int responseCode = connection.getResponseCode();
//                    Log.d(TAG, "responseCode : " + responseCode);
//                    if (responseCode < 400) {
//                        // 请求成功
//                        Log.d(TAG, "节目开始播放快发云get请求成功！！！");
//                        ConfigSettings.countCompletion++;
//                    } else if (responseCode >= 400 && responseCode != 408) { // 表示回调异常
//                        //请求失败,继续请求
//                        Log.e(TAG, "节目开始播放快发云get请求失败,重新发送Get请求！！！");
//                        playProgramByGet(ConfigSettings.COUNT_URL_03);
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (ProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//}
