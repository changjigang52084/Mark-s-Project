package com.sunchip.adw.cloudphotoframe.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtrl {
    public static NetworkUtrl mNetworkUtrl = new NetworkUtrl();

    public NetworkUtrl() {
    }

    public static NetworkUtrl getInstance() {
        if (mNetworkUtrl == null) {
            mNetworkUtrl = new NetworkUtrl();
        }
        return mNetworkUtrl;
    }

    /**
     * 检查当前网络是否可用
     *
     * @return
     */

    public boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null) {
                Log.e("TAG", "当前状态:" + networkInfo.getState());
                // 判断当前网络状态是否为连接状态
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    //判断WiFi是否打开
    public static boolean isWiFi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    //判断移动数据是否打开
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     *
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public synchronized boolean isConnect(String urlStr) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(urlStr)
                    .openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
