package com.lzkj.ui.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月27日 下午3:20:03
 * @parameter 获取当前系统中安装的所有的应用和版本号
 */
public class AppUtil {
    private static final LogTag LOG_TAG = LogUtils.getLogTag(AppUtil.class.getSimpleName(), true);

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = EPosterApp.getApplication().getPackageManager();
            PackageInfo info = manager.getPackageInfo(EPosterApp.getApplication().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用信息
     *
     * @return 应用信息
     */
    public static PackageInfo getPackageInfo() {
        try {
            PackageManager manager = EPosterApp.getApplication().getPackageManager();
            PackageInfo info = manager.getPackageInfo(EPosterApp.getApplication().getPackageName(), 0);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<AppInfo> queryFilterAppInfo() {
        try {
            List<String> runAppProcessNameList = queryRunApp();
            List<AppInfo> appInfos = new ArrayList<AppInfo>();
            PackageManager pm = EPosterApp.getApplication().getPackageManager();
            List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (ApplicationInfo app : listAppcations) {
                String packageName = app.packageName;
                if (packageName.contains("com.android")) {
                    continue;
                }
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                String appName = pm.getApplicationLabel(app).toString();
                int versionCode = packageInfo.versionCode;
                String versioName = packageInfo.versionName;
                AppInfo appInfo = new AppInfo();
                boolean runFlag = false;
                if (runAppProcessNameList != null) {
                    runFlag = runAppProcessNameList.contains(app.processName);
                }
                appInfo.setAppIfRun(runFlag);
                appInfo.setAppName(appName);
                appInfo.setAppPck(packageName);
                appInfo.setAppVersionCode(versionCode);
                appInfo.setAppVersionName(versioName);
                appInfos.add(appInfo);
                LogUtils.d(LOG_TAG, "queryFilterAppInfo", "versionCode：" + versionCode + ",versioName:" + versioName + ",appName：" + appName + ",runFlag：" + runFlag + ",packageName:" + packageName);
            }
            return appInfos;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取正在运行的app
     */
    private static List<String> queryRunApp() {
        try {
            List<String> runAppProcessNameList = new ArrayList<String>();
            ActivityManager activityManager = (ActivityManager) EPosterApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> runAppList = activityManager.getRunningAppProcesses();
            for (RunningAppProcessInfo runningAppProcessInfo : runAppList) {
                String processName = runningAppProcessInfo.processName;
                runAppProcessNameList.add(processName);
            }
            return runAppProcessNameList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 本机是否安装了app
     *
     * @param context        上下文
     * @param appPackageName 包名
     * @return true表示已安装，false未安装
     */
    public static boolean isInstallApp(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo : packageInfos) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                String packageName = packageInfo.packageName;
                if (packageName.equals(appPackageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    //获取MAC地址
    public static String getMacAddress() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    public static ActivityManager.RunningTaskInfo getTopTask() {
        ActivityManager mActivityManager = (ActivityManager) EPosterApp.getApplication().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    public static boolean isTopActivity(ActivityManager.RunningTaskInfo topTask, String packageName, String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;
            LogUtils.i(LOG_TAG, "isTopActivity", "topActivity : " + topActivity.getClassName());
            if (topActivity.getPackageName().equals(packageName) && topActivity.getClassName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }

}
