package com.sunchip.adw.cloudphotoframe.manager;

import android.text.TextUtils;

public class WifiManger {

    public static WifiManger mWifiManger = new WifiManger();

    public WifiManger() {
    }

    public static WifiManger getInstance() {
        if (mWifiManger == null) {
            mWifiManger = new WifiManger();
        }
        return mWifiManger;
    }

    //获取wifi的加密类型
    public String getCipherType(String capabilities) {
        if (!TextUtils.isEmpty(capabilities))
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return "WPA";
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return "WEP";
            }
        return "OPEN";
    }
}
