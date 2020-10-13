package com.unccr.zclh.dsdps.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class AppUtil {

    private static final String TAG = "AppUtil";

    private final static String STOP_APP_ACTION = "com.sunchip.adw.control.app.STOP_APP_ACTION";

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
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            Log.d(TAG, "isServiceWork mName: " + mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * @param packageName 包名
     * @param context     上下文
     * @return boolean 返回类型
     * @Title: isAppRunning
     * @Description: TODO(判断应用是否正在前台运行)
     */
    @TargetApi(Build.VERSION_CODES.Q)
    public static boolean isRunningForeground(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        return componentInfo.getPackageName().equals(packageName);
    }

    /**
     * 启动PlayActivity
     *
     * @param context
     */
    public static void startPlayActivity(Context context) {
        Log.d(TAG, "startPlayActivity. start-up PlayActivity.java");
        ComponentName component = new ComponentName("com.unccr.zclh.dsdps", "com.unccr.zclh.dsdps.play.PlayActivity");
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 调用静默安装apk接口
     */
    public static void installApkForSilent(String appName){
        Log.d(TAG,"installApkForSilent appName: " + appName);
        Intent cmdIntent = new Intent("com.zclhsd.setting.syscmd");
        cmdIntent.putExtra("cmd","appinstall");
        cmdIntent.putExtra("parm",FileUtil.getInstance().getAPPFolder() + File.separator + appName);
        DsdpsApp.getDsdpsApp().sendBroadcast(cmdIntent);
    }

    /**
     * 静默安装app
     */
    public static void installAppForSilent(String appName) {
        String cmd = "pm install -r " + FileUtil.getInstance().getAPPFolder() + File.separator + appName;
        Log.d(TAG, "installAppForSilent cmd: " + cmd);
        Process process = null;
        DataOutputStream dos = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.write(cmd.getBytes());
            dos.writeBytes("\n");
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while ((line = successResult.readLine()) != null) {
                successMsg.append(line);
            }
            while ((line = errorResult.readLine()) != null) {
                errorMsg.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "installAppForSilent IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, "installAppForSilent InterruptedException: " + e.getMessage());
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
            if (successResult != null) {
                try {
                    successResult.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (errorResult != null) {
                try {
                    errorResult.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @return void    返回类型
     * @Title: stopApp
     * @Description: TODO(关闭App)
     * @Param packageName 正常开启，要保留的App包名
     */
    public static void stopApp(String packageName) {
        Log.d(TAG,"stopApp packageName: " + packageName);
        Intent stopAppIntent = new Intent();
        stopAppIntent.setAction(STOP_APP_ACTION);
        stopAppIntent.putExtra("packageName", packageName);
        DsdpsApp.getDsdpsApp().sendBroadcast(stopAppIntent);
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
}
