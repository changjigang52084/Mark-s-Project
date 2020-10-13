package com.sunchip.adw.cloudphotoframe.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.interfaces.ResponseCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Handler;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;


/*
 * 不封装参数交互
 * Created by Administrator on 2019/12/12
 */

public class HttpURLUtils {

    private static String TAG = "HttpURLUtils";

    /*
     * 沒有token 不需要传参
     * */
    public static String doPost(final String urlPath, final ResponseCallback mResponseCallback) {
        final StringBuffer sb = new StringBuffer();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();
                        int len;
                        byte[] b = new byte[1024];
                        while ((len = is.read(b)) != -1) {
                            sb.append(new String(b, 0, len, "utf-8"));
                        }
                        is.close();
                        conn.disconnect();
                    } else {
                        mResponseCallback.onError(conn.getResponseCode(), "请求失败", null);
                    }
                } catch (Exception e) {
                    mResponseCallback.onError(100000000, "请求失败", null);
                }
                mResponseCallback.onResponse(sb.toString());
            }
        }.start();
        return sb.toString();
    }


    /*
     *
     * 没有token的post使用
     * */
    public static String doPost(final String Json, final String urlPath, final ResponseCallback mResponseCallback) {
        final StringBuffer sb = new StringBuffer();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    // 设置文件类型:
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    // 设置接收类型否则返回415错误
                    //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
                    conn.setRequestProperty("accept", "application/json");
                    if (Json != null && !TextUtils.isEmpty(Json)) {
                        byte[] writebytes = Json.getBytes();
                        // 设置文件长度
                        conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                        OutputStream outwritestream = conn.getOutputStream();
                        outwritestream.write(Json.getBytes());
                        outwritestream.flush();
                        outwritestream.close();
                        Log.d("TAG", "doJsonPost: conn " + conn.getResponseCode());
                    }

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();
                        int len;
                        byte[] b = new byte[1024];
                        while ((len = is.read(b)) != -1) {
                            sb.append(new String(b, 0, len, "utf-8"));
                        }
                        is.close();
                        conn.disconnect();
                    } else {
                        mResponseCallback.onError(conn.getResponseCode(), "请求失败", null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mResponseCallback.onResponse(sb.toString());
            }
        }.start();
        return sb.toString();
    }

    /*******************************************************************MD5************************************************************************/
    public static String StringToMD5(String str) {
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(str.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        int i;//定义整型
        //声明StringBuffer对象
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < secretBytes.length; offset++) {
            i = secretBytes[offset];//将首个元素赋值给i
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");//前面补0
            buf.append(Integer.toHexString(i));//转换成16进制编码
        }
        return buf.toString();
    }

    /**********************************************************************sign签名***************************************************************/

    public static String getSign(Map<String, String> param) {
        String[] seq = {"mac", "serial", "type", "time"};
        String token = "VU2B7DFWEU5BN92B39482F9KESBERU4BNSU38G37DVB8JDOR82B378M83SS78WNS";
        if (param.get("type").equals("playlists")) {
            token = "JM83SS78WNSFWEU5BNVU2B7DG37DVB892B39482F9KDOR82B378ESBERU4BNSU38";
        }
        if (param.get("type").equals("heartbest")) {
            token = "ESBEM83SS78WNS82F9KDOR82B378NSU38FWEU5BNVU2B7DG37DVB8RU4BJ92B394";
        }
        if (param.get("type").equals("unpair")) {
            token = "6N6H862F9KDOR8CNHNODG37DVB8RU4BFSERWVERV53245F327B56HJV2345V2567";
        }
        String str = "";
        for (String k : seq) {
            str += k + "=" + param.get(k) + "&&";
        }
        str += "token=" + token;
        str = StringToMD5(str).toUpperCase();
        return str;
    }
    /****************************************************************拼接地址**********************************************************************/
    /**
     * 拼接get请求的url请求地址
     */
    public static String getRqstUrl(String url, Map<String, String> params, Map<String, Long> param) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }

        for (String key : param.keySet()) {
            if (key != null && param.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(param.get(key));
            }
        }

        Log.e("TAG", "URL:" + builder.toString());
        return builder.toString();
    }

    public static String getRqstUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        Log.e("TAG", "URL:" + builder.toString());
        return builder.toString();
    }


    /*
     ***********************************************************************文件判断是否存在****************************************************/
    public static boolean IsFileExists(String path) {
        if (path != null)
            return new File(path).exists();
        else return false;
    }

    /**************************************************************************文件下载*******************************************************/

    public static void Download(final String url, final File saveFile, final int a) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "开始下载=========：" + url);
                HttpRequest.download(url, saveFile, new FileDownloadCallback() {
                    //开始下载
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    //下载进度
                    @Override
                    public void onProgress(int progress, long networkSpeed) {
                        super.onProgress(progress, networkSpeed);
                        Log.e(TAG, "进度值是：" + progress);
                        if (a == 0)
                            EventBus.getDefault().post(new BaseErrarEvent(url, InterFaceCode.DownLoadDone_load));
                        else
                            EventBus.getDefault().post(new BaseErrarEvent(url, InterFaceCode.DownLoadDone_load_Upload));
                    }

                    //下载失败
                    @Override
                    public void onFailure() {
                        super.onFailure();
//                Log.e(TAG, "下载失败的地址是:" + url);
                        //下载失败以后自动再次下载 得先删掉下载失败的文件

                        if (IsFileExists(saveFile.getAbsolutePath())) {
                            //如果存在的话 那么就直接删除这个文件 没有的话直接下载
                            saveFile.delete();
                        }
                        Download(url, saveFile, a);
                    }

                    //下载完成（下载成功）
                    @Override
                    public void onDone() {
                        super.onDone();
                        EventBus.getDefault().post(new BaseErrarEvent(url, InterFaceCode.DownLoadDone));
//                Log.e(TAG, "成功下载的地址是：" + saveFile.getAbsolutePath());
                    }
                });
            }
        };
        new Thread(runnable).start();

    }

    /**************************************************************删除所有文件*************************************************************/
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
        }
    }

    /****************************************************************獲取ip地址********************************************************************/
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
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

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址

                Log.e("TAG", "ipAddress:" + ipAddress);
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}