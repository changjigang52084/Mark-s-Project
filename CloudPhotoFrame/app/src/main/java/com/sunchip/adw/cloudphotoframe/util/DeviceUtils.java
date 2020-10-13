package com.sunchip.adw.cloudphotoframe.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

public class DeviceUtils {

    private static String TAG = "DeviceUtils";

    /**
     * 获取设备名
     *
     * @return 设备名
     */
    public static String getDeviceName() {
        return android.os.Build.DEVICE;
    }

    /*
     * :设备版本号。
     * */
    public static String getDeviceId() {
        return android.os.Build.ID;
    }

    /**
     * 获取设备型号
     *
     * @return 设备型号
     */
    public static String getDeviceMode() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取设备序列号
     *
     * @return 设备序列号
     */
    public static String getDeviceSerialNumber() {
        return Build.SERIAL;
    }

    /**
     * 获取设备版本号
     *
     * @return
     */
    public static String getDeviceFirmware() {
        return android.os.Build.VERSION.RELEASE;
    }


    /**
     * 获取设备iP地址
     *
     * @param context
     * @return
     */
    public static String getIpAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 3/4g网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                // 有限网络
                return getLocalIp();
            }
        }
        return null;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    // 获取有限网IP
    private static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "0.0.0.0";
    }

    //获取本地mac地址
    public static String getMac(Context context) {
        String mac = null;
        if (mac == null) {
            try {
                NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");
                byte[] addrByte = networkInterface.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (byte b : addrByte) {
                    sb.append(String.format("%02X:", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                mac = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                WifiManager wifiM = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                try {
                    WifiInfo wifiI = wifiM.getConnectionInfo();
                    mac = wifiI.getMacAddress();
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                    mac = "02:00:00:00:00:00";
                }
            }
        }
        Log.e(TAG, "MAC:" + mac);
        return mac;
    }

    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 多个SD卡时 取外置SD卡
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        // 参考文章
        // http://blog.csdn.net/bbmiku/article/details/7937745
        Map<String, String> map = System.getenv();
        String[] values = new String[map.values().size()];
        map.values().toArray(values);
        String path = values[values.length - 1];
        if (path.startsWith("/mnt/") && !Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                .equals(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 获取可用空间大小
     *
     * @return
     */
    public static long getAvailaleSize() {
        if (!existSDCard()) {
            return 0l;
        }
        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取SD大小
     *
     * @return
     */
    public static long getAllSize() {
        if (!existSDCard()) {
            return 0l;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize;
    }

    /**
     * 获取本地软件版本号
     */
    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            Log.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            Log.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /*
     * 获取版本号
     */
    public static String getAppVersionName(String pkn, Context mContext) {
        String version = "";
        final PackageManager packageManager = mContext.getPackageManager();
        PackageInfo info;
        try {
            info = packageManager.getPackageInfo(pkn, 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return version;
        }
        return version;
    }

    /*
     * 判断apk是否存在
     */
    public boolean isAppAvilible(String packageName, Context mContext) {
        final PackageManager packageManager = mContext.getPackageManager();
        if (TextUtils.isEmpty(packageName) || null == packageManager) {
            return false;
        }
        try {
            packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    //转换文件大小
    public static String AvailCount(Long availCount) {
        if (availCount < 4831838208L) {
            return "4G";
        } else if (availCount > 4831838208L && availCount < 9126805504L) {
            return "8G";
        } else if (availCount > 9126805504L && availCount < 17716740096L) {
            return "16G";
        } else if (availCount > 17716740096L && availCount < 34896609280L) {
            return "32G";
        }
        return "64G";
    }
}
