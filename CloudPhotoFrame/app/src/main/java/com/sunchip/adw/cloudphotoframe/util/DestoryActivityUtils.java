package com.sunchip.adw.cloudphotoframe.util;

import android.app.Activity;

import java.util.HashMap;

/**
 * Created by yingmuliang on 2020/8/3.
 */
public class DestoryActivityUtils {
    public static DestoryActivityUtils mDestoryActivityUtils = new DestoryActivityUtils();

    public DestoryActivityUtils() {
    }

    public HashMap<String, Activity> hashMap = new HashMap<String, Activity>();

    public static DestoryActivityUtils getInstance() {
        if (mDestoryActivityUtils == null) {
            mDestoryActivityUtils = new DestoryActivityUtils();
        }
        return mDestoryActivityUtils;
    }

    public void addDestoryActivity(Activity activity, String ac) {
        hashMap.put(ac, activity);
    }

    public void destoryActivity() {
        for (String s : hashMap.keySet()) {
            hashMap.get(s).finish();
        }
    }

    public void destoryActivity(String ac) {
        hashMap.get(ac).finish();
    }
}
