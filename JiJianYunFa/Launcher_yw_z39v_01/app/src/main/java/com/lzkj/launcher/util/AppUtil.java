package com.lzkj.launcher.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.lzkj.launcher.app.LauncherApp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月27日 下午3:20:03
 * @parameter 获取当前系统中安装的所有的应用和版本号
 */
public class AppUtil {

    private static final String TAG = "AppUtil";

    /**
     * service 类型
     **/
    public static final String SERVICE_TYPE = "service";
    /**
     * activity 类型
     **/
    public static final String ACTIVITY_TYPE = "activity";

    public static boolean isTopActivity(ActivityManager.RunningTaskInfo topTask, String packageName, String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;
            Log.d(TAG,"isTopActivity className: " + topActivity.getClassName());
            if (topActivity.getPackageName().equals(packageName) && topActivity.getClassName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }

    public static ActivityManager.RunningTaskInfo getTopTask() {
        ActivityManager mActivityManager = (ActivityManager) LauncherApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    /**
     * @param packageName 包名
     * @return boolean 返回类型
     * @Title: isAppRunning
     * @Description: TODO(判断应用是否正在前台运行)
     */
    public static boolean isRunningForeground(String packageName) {
        ActivityManager am = (ActivityManager) LauncherApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        return componentInfo.getPackageName().equals(packageName);
    }

    /**
     * 获取正在运行的app
     */
    private static List<String> queryRunApp() {
        try {
            List<String> runAppProcessNameList = new ArrayList<String>();
            ActivityManager activityManager = (ActivityManager) LauncherApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
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
     * @param packageName
     * @hide
     */
    public static void killProcessToPackageName(String packageName) {
        ActivityManager activityManager = (ActivityManager) LauncherApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return void    返回类型
     * @Title: getAppInfo
     * @Description: TODO(获取应用信息)
     * @Param 设定文件
     */
    public static PackageInfo getAppInfo(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }
        return packageInfo;

    }

    /**
     * @param packageName 包名
     * @param classType   启动类别
     * @return boolean    返回类型
     * @Title: isAppRunning
     * @Description: TODO(判断应是否正在运行)
     */
    public static boolean isAppRunning(String packageName, String classType) {
        ActivityManager am = (ActivityManager) LauncherApp.getApplication().getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : list) {
            String processName = appProcess.processName;
            if (processName != null && processName.equals(packageName)) {
                int status = appProcess.importance;
                if (classType.equals(SERVICE_TYPE)) {
                    return true;
                } else if (classType.equals(ACTIVITY_TYPE)) {
                    if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
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

    /**
     * @param isUpload false
     * @return
     * @throws NameNotFoundException
     */
    public static String getAppInfo(boolean isUpload) throws NameNotFoundException {

        Log.d(TAG, "getAppInfo isUpload: " + isUpload);

        PackageManager manager = LauncherApp.getApplication().getPackageManager();
        String appNameKey = "appName:";
        String versionCodeKey = ",versionCode:";
        String versionNameKey = ",versionName:";
        String lineKey = "\r\n";

        List<String> appPackageNameList = new ArrayList<String>();
        appPackageNameList.add(LauncherApp.getApplication().getPackageName());
        switch (ConfigSettings.INSTALL_APP) {
            case Constant.EPOSTER:
                appPackageNameList.add(Constant.UI_PKG);
                break;
            default:
                break;
        }
        appPackageNameList.add(Constant.DOWNLOAD_PKG);
        appPackageNameList.add(Constant.COMMUNICATION_PKG);

        StringBuffer stringBuffer = new StringBuffer(20);
        StringBuffer appVersion = new StringBuffer(10);
        for (String packageName : appPackageNameList) {
            appVersion.append(packageName);
            PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
            appVersion.append(packageInfo.versionCode);
            stringBuffer.append(packageInfo.applicationInfo.loadLabel(manager));
            stringBuffer.append(versionCodeKey);
            stringBuffer.append(packageInfo.versionCode);
            stringBuffer.append(versionNameKey);
            stringBuffer.append(packageInfo.versionName);
            stringBuffer.append(lineKey);
        }
        String appInfoMsg = ShareUtil.newInstance().getString(ShareUtil.VER_INFO_KEY);
        String appVersionStr = appVersion.toString();
        Log.d(TAG, "getAppInfo appInfoMsg: " + appInfoMsg + " ,appVersionStr: " + appVersionStr);
        if (isUpload) {
            ShareUtil.newInstance().setString(ShareUtil.VER_INFO_KEY, appVersionStr);
        }
        return stringBuffer.toString();
    }
}
