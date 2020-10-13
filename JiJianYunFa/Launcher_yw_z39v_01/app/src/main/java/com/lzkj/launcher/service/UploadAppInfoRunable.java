package com.lzkj.launcher.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.util.AppUtil;
import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.FileStore;
import com.lzkj.launcher.util.ShareUtil;
import com.lzkj.launcher.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年8月20日 下午3:57:37
 * @parameter 上传app当前版本的信息到服务器
 */
public class UploadAppInfoRunable implements Runnable {

    private static final String TAG = "UploadAppInfo";

    @Override
    public void run() {
        try {
            String appInfo = AppUtil.getAppInfo(true);
            Log.d(TAG, "UploadAppInfoRunable appInfo: " + appInfo);
            if (StringUtil.isNullStr(appInfo)) {
                Log.d(TAG, "UploadAppInfoRunable appInfo is null.");
                return;
            }
            String fileName = getFileName();
            Log.d(TAG, "UploadAppInfoRunable fileName: " + fileName);
            File file = new File(FileStore.createTempFolder(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileStore.writerContentToFile(appInfo, file);
            //写入到sd卡，并且上传到七牛
            Log.d(TAG, "UploadAppInfoRunable appPath: " + file.getAbsolutePath());
            sendUploadBroadcast(file.getAbsolutePath());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUploadBroadcast(String filePath) {
        Intent uploadIntent = new Intent();
        uploadIntent.setAction(Constant.UPLOAD_LOG_ACTION);
        uploadIntent.putExtra(Constant.LOG_PATH, filePath);
        LauncherApp.getApplication().sendBroadcast(uploadIntent);
    }

    private String getFileName() throws NameNotFoundException {
        String did = ShareUtil.newInstance().getString(ShareUtil.DEVICE_ID_KEY);
        if (null == did) {
            Context context = LauncherApp.getApplication().createPackageContext(Constant.COMMUNICATION_PKG,
                    Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sharedPreferences = context.getSharedPreferences("pull_parm", Context.MODE_WORLD_READABLE);
            did = sharedPreferences.getString("device_id", null);
            ShareUtil.newInstance().setString(ShareUtil.DEVICE_ID_KEY, did);
        }
        String fileName = "appInfo_" + did + "_" + getCurrentDate() + ".info";

        return fileName;
    }


    /**
     * 显示时间格式为 yyyy_MM_dd_HH_mm_ss
     */
    private String getCurrentDate() {
        String formatStr = "yyyy_MM_dd_HH_mm_ss";
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String temp = sdf.format(new Date());
        return temp;
    }

}
