package com.lzkj.downloadservice.app;

import android.app.Application;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.lzkj.downloadservice.util.ConfigSettings;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FlowManager;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.StorageList;

public class DownloadApp extends Application {
    private static final LogTag TAG = LogUtils.getLogTag(DownloadApp.class.getSimpleName(), true);
    public static final String accessKey = "";
    static final String screctKey = "";

    static {
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;
                return OSSToolKit.generateToken(accessKey, screctKey, content);
            }
        });
        OSSClient.setGlobalDefaultACL(AccessControlList.PRIVATE); // 设置全局默认bucket访问权限
    }

    private static DownloadApp downloadApp;

    public static DownloadApp getContext() {
        return downloadApp;
    }

    @Override
    public void onCreate() {
        downloadApp = this;
        initSdcardPath();
        OSSLog.enableLog(false);
        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
        ConfigSettings.initAdpressCore();
        uploadDownloadTask();
        super.onCreate();
    }

    private void initSdcardPath() {
        StorageList storageList = new StorageList(this);
        FileUtil.getInstance().setSdcard(storageList.getSDPath());
    }

    private void uploadDownloadTask() {
        FlowManager.getInstance().uploadFolwToServer();
    }
}
