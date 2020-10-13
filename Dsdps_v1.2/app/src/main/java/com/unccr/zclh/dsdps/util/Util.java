package com.unccr.zclh.dsdps.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class Util {

    private static final String TAG = "Util";

    /**
     * @param isSuffixTempPrm 是否为本地未下载完的节目列表(后缀为.temp)
     * @return 获取本地的节目列表文件
     * @throws IOException
     */
    public static List<Program> getLocalProgramList(boolean isSuffixTempPrm) throws IOException {
        List<Program> localProgramList = new ArrayList<Program>();

        //首先读取节目文件目录里面所有的节目
        File programFolde = new File(FileStore.getInstance().getLayoutFolderPath());//本地所有节目
        File[] programFileList = programFolde.listFiles();

        for (File programFile : programFileList) {
            Program program = null;
            // 是本地未下载完的节目列表
            if (isSuffixTempPrm) {
                if (programFile.getName().endsWith(FileUtile.TEMP_SUFFIX_NAME)) {
                    if (FileStore.readPrmToFile(programFile) != "" && FileStore.readPrmToFile(programFile) != null) {
                        program = JSON.parseObject(FileStore.readPrmToFile(programFile), Program.class);
                    }
                }
            } else {
                Log.d(TAG, "getLocalProgramList: " + FileStore.readPrmToFile(programFile));
                if (FileStore.readPrmToFile(programFile) != "" && FileStore.readPrmToFile(programFile) != null) {
                    program = JSON.parseObject(FileStore.readPrmToFile(programFile), Program.class);
                }
            }
            if (null != program) {
                localProgramList.add(program);
            }
            program = null;
        }
        return localProgramList;
    }

    public static List<String> getPrmKey(List<Program> programList) {
        List<String> doneDownloadPrm = new ArrayList<String>();
        for (Program program : programList) {
            if (null != program) {
                doneDownloadPrm.add(program.getKey());
            }
        }
        return doneDownloadPrm;
    }

    public static final int REQUEST_CODE_ASK_WRITE_SETTINGS = 101;//允许程序读取或写入系统设置
    public static void requestPermission_ACTION_MANAGE_WRITE_SETTINGS(Activity c){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if( !Settings.System.canWrite(c)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + DsdpsApp.getDsdpsApp().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivityForResult(intent, REQUEST_CODE_ASK_WRITE_SETTINGS);
            }else{
                //有了权限，你要做什么呢？具体的动作
            }
        }
    }

    public static final int REQUEST_OVERLAY = 102;//悬浮窗口权限申请
    public static void requestPermission_ACTION_MANAGE_OVERLAY_PERMISSION(Activity c) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(c)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + DsdpsApp.getDsdpsApp().getPackageName()));
                c.startActivityForResult(intent, REQUEST_OVERLAY);
            } else {

            }
        }
    }

    /**
     * 获取等待下载的节目列表
     *
     * @return
     */
    public static List<String> getWaitPrmList(List<String> remoteProgramList) {
        List<String> waitPrmList = new ArrayList<String>();
        File waitPrmFolder = new File(FileUtile.getInstance().getWaitDownloadPrmFolder());
        String[] fileNames = waitPrmFolder.list();
        for (String fileName : fileNames) {
            StringTokenizer stringTokenizer = new StringTokenizer(fileName, Constants.SPLIT_PATH);
            String prmKey = stringTokenizer.nextToken();
            if (remoteProgramList.contains(prmKey)) {
                remoteProgramList.remove(prmKey);
            }
            Log.d(TAG, "getWaitPrmList prmKey: " + prmKey);
            waitPrmList.add(prmKey);
        }
        return waitPrmList;
    }

    /**
     * 根据用户传入的时间格式返回对应的字符类型
     *
     * @param format 例如:yyyy-MM-dd
     * @return 2015-03-30
     */
    public static String getStringTimeToFormat(String format) {
        if (null == format || "".equals(format)) {
            format = "yyyy-MM-dd";
        }
        return new SimpleDateFormat(format, Locale.CHINA).format(new Date());
    }

    /**
     * @Description: 计算列表aList相对于bList的减少的情况，兼容任何类型元素的列表数据结构
     * @param aList 本列表
     * @param bList 对照列表
     * @return 返回减少的元素组成的列表
     */
    public static <E> List<E> getReduceaListThanbList(List<E> aList, List<E> bList){
        List<E> reduceaList = new ArrayList<E>();
        for (int i = 0; i < bList.size(); i++){
            if(!myListContains(aList, bList.get(i))){
                reduceaList.add(bList.get(i));
            }
        }
        return reduceaList;
    }

    /**
     * @Description: 判断元素element是否是sourceList列表中的一个子元素
     * @param sourceList 源列表
     * @param element 待判断的包含元素
     * @return 包含返回 true，不包含返回 false
     */
    private static <E> boolean myListContains(List<E> sourceList, E element) {
        if (sourceList == null || element == null){
            return false;
        }
        if (sourceList.isEmpty()){
            return false;
        }
        for (E tip : sourceList){
            if(element.equals(tip)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param mContext
     * @param activityClassName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean isActivityRunning(Context mContext, String activityClassName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            ComponentName component = info.get(0).topActivity;
            String className = component.getClassName();
            Log.d(TAG,"isActivityRunning className: " + className);
            if (activityClassName.equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public static String getWifiMacAddress(Context context) {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacFromFile();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }

        return mac.replace(":", "").toUpperCase();
    }

    /**
     * 获取Ethernet的MAC地址
     * @return
     */
    public static String getEthernetMacAddress() {
        try {
            String mac = "02:00:00:00:00:00";
            mac = loadFileAsString("/sys/class/net/eth0/address").toUpperCase(Locale.ENGLISH).substring(0, 17);
            return mac.replace(":","").toUpperCase();
        } catch (IOException e) {
            return null;
        }
    }

    public static String loadFileAsString(String filePath) throws IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024]; int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    private static String getMacFromFile() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = "02:00:00:00:00:00";
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * 获取当天是星期几
     * @return 星期几
     */
    public static int getWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch(dayOfWeek){
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
        }
        return 0;
    }

    /**
     * 获取当天是星期几
     * @return 星期几
     */
    public static String getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch(dayOfWeek){
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return "";
    }
}
