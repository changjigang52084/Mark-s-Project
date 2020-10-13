package com.zhy.http.okhttp.api;

import android.util.Log;

import com.hch.utils.OkhttpConstant;
import com.hch.utils.MLog;
import com.hch.utils.VR_MD5Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.callback.JsonObjectCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/14.
 */
public class OkHttpAPI {

    private static String cancelTag = "";

    public static <T> void postHttpData(String action, String jsonData, GenericsCallback<T> callback) {
        String httpUrl = OkhttpConstant.url + action;
        StringBuffer authorization = new StringBuffer(jsonData)
                .append(OkhttpConstant.MD5_KEY);
        String auth = VR_MD5Util.MD5Authentication(authorization.toString());
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("jsonData", jsonData);
        hashMap.put("ipAddr", OkhttpConstant.IPADDRESS); // 当前参数必须获取客户端真实ID，非服务端ID
        hashMap.put("version", OkhttpConstant.VERSION_CODE_STRING); // 版本号，APP或Web程序发布的版本号
        hashMap.put("authorization", auth);
        // 客户端类型，1表示PC端，2表示IOS,3表示android，4表示M端，5表示微端
        hashMap.put("clientType", "3");
        hashMap.put("identificationCode", OkhttpConstant.DEVICE_NUMBER);
        OkHttpUtils
                .post()//
                .url(httpUrl)
                .params(hashMap).tag(action)
                .build()
                .connTimeOut(10000)
                .execute(callback);
    }
   /**
    *@param action
    * @param jsonData
    * 
    */
    public static void postObjectData(String action, String jsonData, final HttpSuccess<JSONObject> httpSuccess,final HttpError httpError){
        String httpUrl = OkhttpConstant.url + action;
        StringBuffer authorization = new StringBuffer(jsonData)
                .append(OkhttpConstant.MD5_KEY);
        String auth = VR_MD5Util.MD5Authentication(authorization.toString());
        Log.d("cjg","auth: " + auth);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("jsonData", jsonData);
        hashMap.put("ipAddr", OkhttpConstant.IPADDRESS); // 当前参数必须获取客户端真实ID，非服务端ID
        hashMap.put("version", OkhttpConstant.VERSION_CODE_STRING); // 版本号，APP或Web程序发布的版本号
        hashMap.put("authorization", auth);
        // 客户端类型，1表示PC端，2表示IOS,3表示android，4表示M端，5表示微端
        hashMap.put("clientType", "3");
        hashMap.put("identificationCode", OkhttpConstant.DEVICE_NUMBER);
        OkHttpUtils
                .post()//
                .url(httpUrl)//
                .params(hashMap)
                .build()
                .connTimeOut(10000)
                .execute(new JsonObjectCallback() {
                    @Override
                    public   void onError(Call call, Exception e, int id) {
                        httpError.onError(call,e,id);
                    }

                    @Override
                    public void onResponse(JSONObject response, int id) {
                        httpSuccess.onSuccess(response,id);
                    }
                });
    }

    public static void cancelTag(String callTag){
        OkHttpUtils.getInstance().cancelTag(callTag);
        cancelTag = callTag;
    }


    public static <T> void posTestObjectData(String action, String mac,String sn,String time,String sign,String key,String appVersion , final HttpSuccess<JSONObject> httpSuccess,final HttpError httpError) throws JSONException {
        String httpUrl = OkhttpConstant.zc_url + action;
        Log.d("cjg","httpUrl: " + httpUrl);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("mac", mac);
        hashMap.put("sn", sn);
        hashMap.put("key", key);
        hashMap.put("time", time);
        hashMap.put("sign", sign);
        hashMap.put("app_version", appVersion);
        hashMap.put("state","1");
        OkHttpUtils
                .post()//
                .url(httpUrl)
                .params(hashMap).tag(action)
                .build()
                .connTimeOut(10000)
                .execute(new JsonObjectCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        httpError.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(JSONObject response, int id) {
                        httpSuccess.onSuccess(response, id);
                    }
                });
    }

}
