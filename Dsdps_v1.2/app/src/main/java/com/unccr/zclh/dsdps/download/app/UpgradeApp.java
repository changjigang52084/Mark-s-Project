package com.unccr.zclh.dsdps.download.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpgradeApp implements Runnable {

    private static final String TAG = "UpgradeApp";

    private String downloadUrl;
    private Handler completeHandler;
    private File appFile;
    private String appName;

    public UpgradeApp(String downloadUrl, Handler completeHandler, File appFile, String appName) {
        this.downloadUrl = downloadUrl;
        this.completeHandler = completeHandler;
        this.appFile = appFile;
        this.appName = appName;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            // 2865412
            long length = connection.getContentLength();
            // 每次读取1K
            byte[] buffer = new byte[4096];
            int len = -1;
            FileOutputStream fos = new FileOutputStream(appFile);
            int temp = 0;
            while ((len = is.read(buffer)) != -1) {
                // 写入文件
                fos.write(buffer, 0, len);
                // 当前进度
                int schedule = (int) ((appFile.length() * 100) / length);
                // 通知更新进度（10，7，4整除才通知，没必要每次都更新进度）
                if (temp != schedule && (schedule % 10 == 0 || schedule % 4 == 0 || schedule % 7 == 0)) {
                    // 保证同一个数据只发了一次
                    temp = schedule;
//                    Log.d(TAG,"schedule: " + schedule + " ,temp: " + temp);
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("appName", appName);
                    bundle.putInt("schedule", schedule);
                    message.setData(bundle);
                    completeHandler.sendMessage(message);
                }
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
