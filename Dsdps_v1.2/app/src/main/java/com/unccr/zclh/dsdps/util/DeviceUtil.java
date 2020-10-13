package com.unccr.zclh.dsdps.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.unccr.zclh.dsdps.app.DsdpsApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Random;

public class DeviceUtil {

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
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

    /**
     * 获取公网IP地址
     *
     * @param context
     * @return
     */
//    public void GetNetIpAddress(Context context) {
//        new Thread() {
//            @Override
//            public void run() {
//                String line = "";
//                URL infoUrl = null;
//                InputStream inStream = null;
//                try {
//                    infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
//                    URLConnection connection = infoUrl.openConnection();
//                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
//                    int responseCode = httpConnection.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        inStream = httpConnection.getInputStream();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
//                        StringBuilder strber = new StringBuilder();
//                        while ((line = reader.readLine()) != null)
//                            strber.append(line + "\n");
//                        inStream.close();
//                        // 从反馈的结果中提取出IP地址
//                        int start = strber.indexOf("{");
//                        int end = strber.indexOf("}");
//                        String json = strber.substring(start, end + 1);
//                        if (json != null) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(json);
//                                line = jsonObject.optString("cip");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        Message msg = new Message();
//                        msg.what = 1;
//                        msg.obj = line;
//                        //向主线程发送消息
//                        handler.sendMessage(msg);
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }

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

    /**
     * 获取系统运行内存大小
     *
     * @param mContext
     * @return
     */
    public static long getTotalMemory(Context mContext) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //return Formatter.formatFileSize(mContext, mi.totalMem);// 将获取的内存大小规格化
        return mi.totalMem;
    }

    /**
     * 重启设备
     */
    public static void rebootDevice() {
        Intent cmdIntent = new Intent("android.intent.action.reboot");
        DsdpsApp.getDsdpsApp().sendBroadcast(cmdIntent);
    }

    /**
     * 待机
     */
    public static void standbyDevice() {
        Intent cmdIntent = new Intent("android.intent.action.gotosleep");
        DsdpsApp.getDsdpsApp().sendBroadcast(cmdIntent);
    }

    /**
     * 唤醒
     */
    public static void wakeUpDevice() {
        Intent cmdIntent = new Intent("android.intent.action.exitsleep");
        DsdpsApp.getDsdpsApp().sendBroadcast(cmdIntent);
    }

    /**
     * 关机
     */
    public static void shutdownDevice() {
        SharedUtil.newInstance().setInt("state", 2);
//        Intent cmdIntent = new Intent("com.zclhsd.setting.syscmd");
//        cmdIntent.putExtra("cmd","poweroff");
        String commandToRun = "adb shell am broadcast -a android.intent.action.shutdown";
        try {
            execByRuntime(commandToRun);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        DsdpsApp.getDsdpsApp().sendBroadcast(cmdIntent);
    }

    /**
     * 执行shell 命令， 命令中不必再带 adb shell
     *
     * @param cmd
     * @return Sting  命令执行在控制台输出的结果
     */

    private static String execByRuntime(String cmd) {
        Process process = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = bufferedReader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != inputStreamReader) {
                try {
                    inputStreamReader.close();
                } catch (Throwable t) {

                }
            }
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (Throwable t) {

                }
            }
            if (null != process) {
                try {
                    process.destroy();
                } catch (Throwable t) {

                }
            }
        }
    }
}
