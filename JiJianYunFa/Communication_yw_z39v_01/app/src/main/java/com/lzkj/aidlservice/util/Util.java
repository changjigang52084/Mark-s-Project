package com.lzkj.aidlservice.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月8日 下午8:15:46
 * @parameter
 */
public class Util {
    private static final LogTag TAG = LogUtils.getLogTag(Util.class.getSimpleName(), true);

    /**
     * 请求类型  终端机注册请求
     */
    public static final int REGISTERED_REQUEST = 0x020005;
    /**
     * 请求类型  终端机恢复注册信息请求
     */
    public static final int REGISTERED_RECOVERY = 0x020003;

    /**
     * @param isSuffixTempPrm 是否为本地未下载完的节目列表(后缀为.temp)
     * @return 获取本地的节目列表文件
     * @throws IOException
     */
    public static List<Program> getLocalProgramList(boolean isSuffixTempPrm) throws IOException {
        List<Program> localProgramList = new ArrayList<Program>();

        //首先读取节目文件目录里面所有的节目
        File programFolde = new File(FileUtile.getInstance().getLayoutFolderPath());//本地所有节目
        File[] programFileList = programFolde.listFiles();

        for (File programFile : programFileList) {
            Program program = null;
            // 是本地未下载完的节目列表
            if (isSuffixTempPrm) {
                if (programFile.getName().endsWith(FileUtile.TEMP_SUFFIX_NAME)) {
                    if (FileUtile.readPrmToFile(programFile) != "" && FileUtile.readPrmToFile(programFile) != null) {
                        program = JSON.parseObject(FileUtile.readPrmToFile(programFile), Program.class);
                    }
                }
                // 不是本地未下载完的节目列表
            } else {
                LogUtils.d(TAG, "ReadPrmToFile", FileUtile.readPrmToFile(programFile));
                if (FileUtile.readPrmToFile(programFile) != "" && FileUtile.readPrmToFile(programFile) != null) {
                    program = JSON.parseObject(FileUtile.readPrmToFile(programFile), Program.class);
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
            StringTokenizer stringTokenizer = new StringTokenizer(fileName, Constant.SPLIT_PATH);
            String prmKey = stringTokenizer.nextToken();
            if (remoteProgramList.contains(prmKey)) {
                remoteProgramList.remove(prmKey);
            }
            LogUtils.d(TAG, "getWaitPrmList", "prmKey : " + prmKey);
            waitPrmList.add(prmKey);
        }
        return waitPrmList;
    }

    /**
     * 获取Android Mac地址或设备ID
     *
     * @return 12位Mac地址或16位的设备ID
     */
    public static String getMacAddress() {
        Context context = CommunicationApp.get();
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
            LogUtils.d(TAG, "getMacAddress", macAddress);
        }
        return macAddress;
    }

    /*
     * Load file content to String
     */
    public static String loadFileAsString(String filePath) throws IOException {
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

    public static int getRealDisplayWidth() {
        int widthtPixels;
        WindowManager w = ((WindowManager) CommunicationApp.get().getSystemService(Context.WINDOW_SERVICE));
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        widthtPixels = metrics.widthPixels;
        // includes window decorations (statusbar bar/navigation bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthtPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
            } catch (Exception e) {
            }
        } else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthtPixels = realSize.x;
            } catch (Exception e) {
            }
        }
        return widthtPixels;
    }

    public static int getRealDisplayHeight() {
        int heightPixels;
        WindowManager w = ((WindowManager) CommunicationApp.get().getSystemService(Context.WINDOW_SERVICE));
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/navigation bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // includes window decorations (statusbar bar/navigation bar)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception e) {
            }
        }
        return heightPixels;
    }

    /**
     * 获取当天是星期几
     *
     * @return 星期几
     */
    public static int getWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
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
     * 获取当前的时分，并且转换成毫秒
     *
     * @param format 时间字符串的格式(默认 HH:mm:SS)
     * @return 时间毫秒
     */
    public static long getCurrentMillisTime(String format) {
        if (StringUtil.isNullStr(format)) {
            format = "HH:mm:SS";
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(dateFormat.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前时间离关机时间还剩多少毫秒
     *
     * @param shutdown 关机时间
     * @return 剩余多少毫秒关机
     */
    public static int getShutdownTime(String shutdown) {
        long shutdownLong = getMillisFromStringTime(shutdown);
        int time = (int) (shutdownLong - System.currentTimeMillis());
        System.out.println("shutdownLong:" + shutdownLong + ",time" + time);
        return time;
    }


    public static long getMillisFromStringTime(String time) {
        Calendar cal = Calendar.getInstance();
        String[] timeArray = time.split(":"); // HH:MM
        int hour = Integer.parseInt(timeArray[0]);
        int minute = Integer.parseInt(timeArray[1]);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), hour, minute, 0);
        return (long) (cal.getTimeInMillis() * 0.001) * 1000;
    }

    /**
     * 获取uuid
     *
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号  
        String temp = str.replaceAll("-", "");
        return temp;
    }

    /**
     * @param date yyyy-MM-dd
     * @return
     */
    public static long getTimeToDate(String date) {
        long millis = -1;
        try {
            millis = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(date).getTime();
        } catch (ParseException e) {
        }

        return millis;
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
     * 获取当前时间的前n天日期
     *
     * @param front 前多少天负数,后多少天正数,例如:当前日期2015-09-28 传入-2则返回2015-09-26,传入2则返回2015-09-30
     * @return
     */
    public static String getCurrentFrontDate(int front) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, front);
        String preMonday = sdf.format(c.getTime());
        return preMonday;
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
     * 重启
     */
    public static void reboot() {
        try {
            LogUtils.d(TAG, "reboot", "reboot");
            String cmd = "su -c reboot";
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
