package com.xunixianshi.vrshow.util;

import android.os.Environment;

import com.hch.utils.SDCardUtil;
import com.xunixianshi.vrshow.obj.MainClassifyHomeColumnListObj;

import java.util.ArrayList;

/**
 * app配置常量集合类
 * @ClassName AppContent
 *@author HeChuang
 *@time 2016/11/1 15:52
 */
public class AppContent {
    public static String UID = "";
    public static String USER_PROTOCOL = "http://www.vrshow.com/userLicense";
    public static boolean fromWelcome = false; //  是否是新开引用  上一个页面是否是 欢迎页
    /**
     * 是否是内部存储
     */
    public static boolean isInternalStorage = true;
    // 内部存储路径
    public static String InternalStorage = Environment.getExternalStorageDirectory().getPath();
    // sd卡存储路径
    public static String SDCardStorage = SDCardUtil.getExtSDCardPath() + "/download";
    public static int LIMIT_LOGIN = 0;
}
