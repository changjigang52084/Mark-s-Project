package com.sunchip.adw.cloudphotoframe.http.request;

import android.content.Context;
import android.util.Log;

import com.example.SunchipFile.File_Message;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.HttpMethods;
import com.sunchip.adw.cloudphotoframe.http.HttpURLUtils;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.interfaces.ResponseCallback;
import com.sunchip.adw.cloudphotoframe.util.DeviceUtils;
import com.sunchip.adw.cloudphotoframe.util.InitializationUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.NotDialogSystemUI;


public class HttpRequestAuxiliary {


    private String SN = DeviceUtils.getDeviceSerialNumber();

    public static HttpRequestAuxiliary mHttpRequestAuxiliary = new HttpRequestAuxiliary();

    public HttpRequestAuxiliary() {
    }

    public static HttpRequestAuxiliary getInstance() {
        if (mHttpRequestAuxiliary == null) {
            mHttpRequestAuxiliary = new HttpRequestAuxiliary();
        }
        return mHttpRequestAuxiliary;
    }


    private String Mac = DeviceUtils.getMac(CloudFrameApp.getCloudFrameApp());


    /*
     * 返回数据  允许播放的数据  暂时固定死资源
     * */

    public void getPlayList(final int code) {

        Map<String, String> params = new HashMap<>();
        params.put("mac", Mac);
        params.put("serial", SN);
        params.put("time", String.valueOf(System.currentTimeMillis()));
        params.put("type", "playlists");
        params.put("sign", HttpURLUtils.getSign(params));

        String Url = HttpURLUtils.getRqstUrl(HttpMethods.METHOD_FRAME_PLAYLIST, params);

        HttpURLUtils.doPost(Url, new ResponseCallback() {
            @Override
            public void onResponse(String string) {
                //获取到的资源文本
                //使用event去刷新内容
                Log.e("TAG", "允许播放的数据:" + string);

                if (string!=null && !"".equals(string) && string.length()>10) {
                    //直接保存一份到本地
                    File_Message.write(string, "PhotoList", CloudFrameApp.getCloudFrameApp());
                }
                EventBus.getDefault().post(new BaseErrarEvent(string, code));
            }

            @Override
            public void onError(int code, String ErrarString, Throwable throwable) {

            }

            @Override
            public void onException(String s) {

            }

            @Override
            public void UploadProBar() {

            }
        });
    }

    /*
     * 在线心跳
     * */

    public void getheartbest(final int code) {

        HttpURLUtils.doPost(getUrl(), new ResponseCallback() {
            @Override
            public void onResponse(String string) {
                Log.e("TAG", "在线心跳返回內容:" + string);
                //获取到的资源文本
                //使用event去刷新内容
                EventBus.getDefault().post(new BaseErrarEvent(string, code));
            }

            @Override
            public void onError(int code, String ErrarString, Throwable throwable) {
                Log.e("TAG", "在线心跳錯誤嗎:" + code + "   ErrarString:" + ErrarString);
            }

            @Override
            public void onException(String s) {
                Log.e("TAG", "onException:" + s);
            }

            @Override
            public void UploadProBar() {

            }
        });
    }

    //单独解绑
    public void ResyneFrame(final int code) {

        Map<String, String> params = new HashMap<>();
        params.put("mac", Mac);
        params.put("serial", SN);
        params.put("time", String.valueOf(System.currentTimeMillis()));
        params.put("type", "unpair");
        params.put("sign", HttpURLUtils.getSign(params));

        String Url = HttpURLUtils.getRqstUrl(HttpMethods.METHOD_FRAME_RESYNE, params);

        HttpURLUtils.doPost(Url, new ResponseCallback() {
            @Override
            public void onResponse(String string) {
                //获取到的资源文本
                //使用event去刷新内容
                Log.e("TAG", "单独解绑返回内容" + string);
                EventBus.getDefault().post(new BaseErrarEvent(string, code));
            }

            @Override
            public void onError(int code, String ErrarString, Throwable throwable) {

            }

            @Override
            public void onException(String s) {

            }

            @Override
            public void UploadProBar() {

            }
        });
    }


    //心跳更改配置文件
    public void setSetting(final int code, final Context context) {
        EventBus.getDefault().post(new BaseErrarEvent("", NotDialogSystemUI));
        HttpURLUtils.doPost(InitializationUtils.getInstance().setGetHolder(context), getUrl(), new ResponseCallback() {
            @Override
            public void onResponse(String string) {
                Log.e("TAG", "在线心跳返回更改设置的內容:" + string + "       context:" + context.toString());
                //获取到的资源文本
                //使用event去刷新内容
                EventBus.getDefault().post(new BaseErrarEvent(string, code));
            }

            @Override
            public void onError(int code, String ErrarString, Throwable throwable) {

            }

            @Override
            public void onException(String s) {

            }

            @Override
            public void UploadProBar() {

            }
        });
    }


    public String getUrl() {
        Map<String, String> params = new HashMap<>();
        params.put("mac", Mac);
        params.put("serial", SN);
        params.put("time", String.valueOf(System.currentTimeMillis()));
        params.put("type", "heartbest");
        params.put("sign", HttpURLUtils.getSign(params));
        params.put("regId", CloudFrameApp.regId);
        try {
            params.put("ip", HttpURLUtils.getIPAddress(CloudFrameApp.getCloudFrameApp()).toString());
        } catch (Exception e) {
            Log.e("TAG", "请链接网络.......");
        }

        params.put("softwareVersion", DeviceUtils.getLocalVersionName(CloudFrameApp.getCloudFrameApp()));
        Map<String, Long> paramint = new HashMap<>();
        paramint.put("spaceTotal", DeviceUtils.getAllSize());
        paramint.put("spaceFree", DeviceUtils.getAvailaleSize());

        return HttpURLUtils.getRqstUrl(HttpMethods.METHOD_FRAME_STATE, params, paramint);
    }
}
