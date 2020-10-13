package com.lzkj.aidlservice.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.bo.AppInfo;

import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
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

    private final static String STOP_APP_ACTION = "com.lzkj.control.app.STOP_APP_ACTION";

    /**
     * service 类型
     **/
    public static final String SERVICE_TYPE = "service";
    /**
     * activity 类型
     **/
    public static final String ACTIVITY_TYPE = "activity";

    public static List<AppInfo> queryFilterAppInfo() {
        try {
            List<String> runAppProcessNameList = queryRunApp();
            List<AppInfo> appInfos = new ArrayList<AppInfo>();
            PackageManager pm = CommunicationApp.get().getPackageManager();
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
//				LogUtils.d(LOG_TAG, "queryFilterAppInfo", "versionCode："+versionCode+",versioName:"+versioName+",appName："+appName+",runFlag："+runFlag+",packageName:"+packageName);
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
            ActivityManager activityManager = (ActivityManager) CommunicationApp.get().getSystemService(Context.ACTIVITY_SERVICE);
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
        ActivityManager activityManager = (ActivityManager) CommunicationApp.get().getSystemService(Context.ACTIVITY_SERVICE);
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
        ActivityManager am = (ActivityManager) CommunicationApp.get().getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : list) {
            String processName = appProcess.processName;
//			LogUtils.d(LOG_TAG, "isAppRunning", "processName: " + processName);
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
//				LogUtils.d(LOG_TAG, "isServiceWork", "mName : " + mName);
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
    public synchronized static boolean isInstallApp(Context context, String appPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            synchronized (context) {
                List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
                for (PackageInfo packageInfo : packageInfos) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                        String packageName = packageInfo.packageName;
                        if (packageName.equals(appPackageName)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 是否安装了dsplug和socail 2 个应用
     *
     * @return true表示是安装了
     */
    public static boolean isInstallDSplugAndSocail() {
        boolean isInstallAll = false;
        return isInstallAll;
    }

    /**
     * @param packageName 包名
     * @param context     上下文
     * @return boolean 返回类型
     * @Title: isAppRunning
     * @Description: TODO(判断应用是否正在前台运行)
     */
    public static boolean isRunningForeground(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        return componentInfo.getPackageName().equals(packageName);
    }

    /**
     * @return void    返回类型
     * @Title: stopApp
     * @Description: TODO(关闭App)
     * @Param packageName 正常开启，要保留的App包名
     */
    public static void stopApp(String packageName) {
        Intent stopAppIntent = new Intent();
        stopAppIntent.setAction(STOP_APP_ACTION);
        stopAppIntent.putExtra("packageName", packageName);
        CommunicationApp.get().sendBroadcast(stopAppIntent);
    }

    public static ActivityManager.RunningTaskInfo getTopTask() {
        ActivityManager mActivityManager = (ActivityManager) CommunicationApp.get().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    public static boolean isTopActivity(ActivityManager.RunningTaskInfo topTask, String packageName, String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;
            Log.d("AppUtil","isTopActivity className: " + topActivity.getClassName());
            if (topActivity.getPackageName().equals(packageName) && topActivity.getClassName().equals(activityName)) {
                return true;
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

    public static boolean reBoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="reboot";
            //切换到root帐号
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 提示epost更新节目列表
     */
    public static void notifyProgramPlayList(){
        Intent notifyIntent = new Intent(Constant.NOTFIY_PRM_ACTION);
        CommunicationApp.get().sendBroadcast(notifyIntent);
    }
}
