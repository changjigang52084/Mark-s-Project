package com.lzkj.launcher.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.lzkj.launcher.app.LauncherApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    private static final String TAG = "Helper";

    /**
     * 获取当前时间字符串
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", ConfigSettings.SYSTEM_LOCALE).format(new Date());
    }

    /**
     * 获取Android Mac地址或设备ID
     *
     * @return 12位Mac地址或16位的设备ID
     */
    public static String getMacAddress() {
        Context context = LauncherApp.getApplication();
        String macAddress = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID).toUpperCase();
        //gw,sx,yyt的板卡，只能用wifi Mac地址，wifi没打开，则提示打开wifi.
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiMgr.setWifiEnabled(true);
        if (wifiMgr != null) {
            int maxRetry = 10;
            int retry = 0;
            WifiInfo info = wifiMgr.getConnectionInfo();
            String s = info.getMacAddress();
            try {
                while (TextUtils.isEmpty(s)) {
                    retry++;
                    if (retry > maxRetry) {
                        return macAddress;
                    }
                    info = wifiMgr.getConnectionInfo();
                    s = info.getMacAddress();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            macAddress = info.getMacAddress().replace(":", "").toUpperCase();
            Log.w(TAG, "Helper getMacAddress macAddress: " + macAddress);
        }
        return macAddress;
    }

    /*
     * Load file content to String
     */
    public static String loadFileAsString(String filePath)
            throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * 获取三个app的包名和路径
     *
     * @return 返回包含app路径和包名的map对象
     */
    public static Map<String, String> getAppPathAndPackage() {
        Map<String, String> appMapPath = new HashMap<String, String>();
        appMapPath.put(Constant.COMMUNICATION_PKG, FileStore.getCommunicationAppPath());
        appMapPath.put(Constant.DOWNLOAD_PKG, FileStore.getDownloadAppPath());
        appMapPath.put(Constant.UI_PKG, FileStore.getEpostAppPath());
        checkInstallWhilcPlay(appMapPath, true);
        return appMapPath;
    }

    /**
     * 判断安装哪个播放的app
     *
     * @param appMapPath
     * @param isInstall
     */
    private static void checkInstallWhilcPlay(Map<String, String> appMapPath, boolean isInstall) {
        switch (ConfigSettings.INSTALL_APP) {
            case Constant.EPOSTER:
                if (isInstall) {
                    appMapPath.put(Constant.UI_PKG, FileStore.getEpostAppPath());
                } else {
                    appMapPath.put(Constant.UI_PKG, Constant.EPOSTER_VERSION);//版本号
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取最新的app版本号
     *
     * @return 返回最新app版本号
     */
    public static Map<String, String> getAppNewVersion() {
        Map<String, String> newAppVersionMap = new HashMap<String, String>();
        newAppVersionMap.put(Constant.COMMUNICATION_PKG, Constant.COMM_VERSION);
        newAppVersionMap.put(Constant.DOWNLOAD_PKG, Constant.DOWNLOAD_VERSION);
        checkInstallWhilcPlay(newAppVersionMap, false);
        return newAppVersionMap;
    }
}
