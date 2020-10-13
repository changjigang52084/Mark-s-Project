package com.lzkj.launcher.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.lzkj.baize_android.utils.AppUtils;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.bo.InstallBo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年3月15日 下午8:17:01
 * @parameter 安装app的工具类
 */
public class InstallAppTool {

    private static final String TAG = "InstallAppTool";

    private static final String HEART_SERVICE_NAME = "com.lzkj.aidlservice.api.heart.HeartService";

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    //首先判断当前应用里面是否已经安装了 EPosterUI,DownloadService,Communication
    public static void checkAppIsInstall(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isUnzipApp(context);
                Log.d(TAG, "InstallAppTool checkAppIsInstall isUnzipApp: " + isUnzipApp(context));
                Map<String, String> appPackageAndPathMap = getUninstallAppMap(context);
                Log.d(TAG, "InstallAppTool checkAppIsInstall appPackageAndPathMap size: " + appPackageAndPathMap.size() + " ,isExistsZip: " + isExistsZip(context));
                if (!appPackageAndPathMap.isEmpty() && isExistsZip(context)) {
                    Map<String, String> updateAppMap = checkIsUpdateRelyApp(context);
                    if (!updateAppMap.isEmpty()) {
                        appPackageAndPathMap.putAll(updateAppMap);
                    }
                    timerTask(appPackageAndPathMap);
                } else {
                    Map<String, String> updateAppMap = checkIsUpdateRelyApp(context);
                    if (!updateAppMap.isEmpty()) {
                        unzip(context);
                        appPackageAndPathMap.putAll(updateAppMap);
                    }
                }
                for (Map.Entry<String, String> entry : appPackageAndPathMap.entrySet()) {
                    LogUtils.d(LogUtils.getStackTraceElement(), entry.getValue());
                    installApp(context, entry.getValue());
                }
            }
        }).start();
    }


    /**
     * 判断是否有zip文件
     *
     * @param context
     * @return true表示存在
     */
    private static boolean isExistsZip(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(FileStore.APP_NAME);
            if (null != inputStream) {
                inputStream.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 应用是否解压缩
     *
     * @param context
     * @return
     */
    private static boolean isUnzipApp(Context context) {
        boolean isUnzip = false;
        FileStore.createAppFolder();
        if (isUnzip()) {
            isUnzip = true;
            unzip(context);
        }
        return isUnzip;
    }

    /**
     * 获取未安装的app map
     *
     * @param context
     * @return
     */
    private static Map<String, String> getUninstallAppMap(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Map<String, String> appPackageAndPathMap = Helper.getAppPathAndPackage();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo : packageInfos) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                String packageName = packageInfo.packageName;
                if (appPackageAndPathMap.containsKey(packageName)) {
                    Log.d(TAG, "InstallAppTool getUninstallAppMap remove packageName: " + packageName);
                    appPackageAndPathMap.remove(packageName);
                }
            }
        }
        return appPackageAndPathMap;
    }

    /**
     * 判断当前安装的app是否为最新的app
     *
     * @param context
     * @return 返回需要安装的app map
     */
    private static Map<String, String> checkIsUpdateRelyApp(Context context) {
        Map<String, String> appPackageAndPathMap = Helper.getAppPathAndPackage();
        Map<String, String> appPackageAndVersionMap = Helper.getAppNewVersion();
        for (Map.Entry<String, String> versionEntry : appPackageAndVersionMap.entrySet()) {
            int versionCode = getVersion(versionEntry.getKey(), context);
            int newVersionCode = Integer.parseInt(versionEntry.getValue());
            Log.d(TAG, "InstallAppTool checkIsUpdateRelyApp packageName: " + versionEntry.getKey() + " ,versionCode: " + versionCode + " ,newVersionCode: " + newVersionCode);
            if (versionCode >= newVersionCode) {
                appPackageAndPathMap.remove(versionEntry.getKey());
            }
        }
        return appPackageAndPathMap;
    }

    /**
     * 解压缩app.zip
     *
     * @param context
     */
    private static void unzip(Context context) {
        Log.d(TAG, "InstallAppTool unzip true.");
        try {
            UnzipFromAssets.unZip(context, FileStore.APP_NAME, FileStore.APP_FOLDER, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    private static int getVersion(String pck, Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            if (StringUtil.isNullStr(pck) || manager == null) {
                return 0;
            }
            PackageInfo info = manager.getPackageInfo(pck, 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 启动定时任务
     */
    private static void timerTask(final Map<String, String> appPackageAndPathMap) {
        final long delayMillis = 3000;
        LauncherApp.getApplication().mLauncherHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断是否安装了communication apk
                boolean isInstallCommunication = AppUtil.isInstallApp(LauncherApp.getApplication(), Constant.COMMUNICATION_PKG);
                Log.d(TAG, "InstallAppTool timerTask isInstallCommunication: " + isInstallCommunication);
                if (isInstallCommunication) {//安装了就启动service
                    if (!AppUtil.isServiceWork(LauncherApp.getApplication(), HEART_SERVICE_NAME)) {
                        Log.d(TAG, "InstallAppTool timerTask start heartService.");
                        ComponentName componentName = new ComponentName(Constant.COMMUNICATION_PKG, Constant.COMMUNICATION_HEART_CLS);
                        Intent heartServiceIntent = new Intent();
                        heartServiceIntent.setComponent(componentName);
                        LauncherApp.getApplication().startService(heartServiceIntent);
                        LauncherApp.getApplication().mLauncherHandler.postDelayed(this, delayMillis);
                    }
                } else {
                    LauncherApp.getApplication().mLauncherHandler.postDelayed(this, delayMillis);
                }
            }
        }, delayMillis);
    }

    /**
     * 启动心跳服务
     */
    private static void startHeartService(Map<String, String> appPackageAndPathMap) {
        PackageManager packageManager = LauncherApp.getApplication().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo : packageInfos) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                String packageName = packageInfo.packageName;
                if (Constant.COMMUNICATION_PKG.equals(packageName)) {
                    ComponentName componentName = new ComponentName(Constant.COMMUNICATION_PKG,
                            Constant.COMMUNICATION_HEART_CLS);
                    Intent heartServiceIntent = new Intent();
                    heartServiceIntent.setComponent(componentName);
                    if (!AppUtils.isServiceWork(LauncherApp.getApplication(), Constant.COMMUNICATION_HEART_CLS)) {
                        LauncherApp.getApplication().startService(heartServiceIntent);
                        timerTask(appPackageAndPathMap);
                    }
                }
                Log.d(TAG, "InstallAppTool startHeartService packageName: " + packageName);
            }
        }
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


    private static void stopEPoster(String packageName) {
        if (Constant.UI_PKG.equals(packageName)) {
            Intent intent = new Intent(Constant.STOP_APP_ACTION);
            LauncherApp.getApplication().sendBroadcast(intent);
        }
    }

    /**
     * 是否要解压app
     *
     * @return true表示要解压, false表示不需要解压
     */
    private static boolean isUnzip() {
        switch (ConfigSettings.INSTALL_APP) {
            default:
                break;
        }

        File epostFile = new File(FileStore.getEpostAppPath());
        if (!epostFile.exists()) {
            return true;
        }

        File downloadFile = new File(FileStore.getDownloadAppPath());
        if (!downloadFile.exists()) {
            return true;
        }

        File communicationFile = new File(FileStore.getCommunicationAppPath());
        if (!communicationFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 安装未安装的应用
     * 安装并启动apk
     *
     * @param context 上下文对象
     * @param path    app应用的路径
     */
    private static void installApp(Context context, final String path) {
        Log.d(TAG, "InstallAppTool installApp path: " + path);
        if (new File(path).exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    private static InstallBo execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return null;
        }
        InstallBo installBo = new InstallBo();
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
            installBo.errorMsg = errorMsg.toString();
            installBo.successMsg = successMsg.toString();
            Log.d(TAG,"InstallAppTool execCommand successMsg: " + successMsg + " ,errorMsg: " + errorMsg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return installBo;
    }

    /**
     * 重启
     */
    private static void rebootTimer() {
        try {
            String cmd = "su -c reboot";
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
