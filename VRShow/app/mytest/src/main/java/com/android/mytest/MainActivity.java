package com.android.mytest;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mytest.bean.LoginBean;
import com.android.mytest.bean.UserMessageResult;
import com.android.mytest.utils.DeviceUtil;
import com.android.mytest.utils.LoadingAnimationDialog;
import com.android.mytest.utils.MacUtil;
import com.android.mytest.utils.PackageUtil;
import com.android.mytest.utils.RasUtil;
import com.android.mytest.utils.StringUtil;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private LoadingAnimationDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSerialNumber();
        Log.d("cjg", "deviceNumber: " + OkhttpConstant.DEVICE_NUMBER);
        progressDialog = new LoadingAnimationDialog(MainActivity.this);
        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getValidateCode();
//                goToRegister();
//                heartBeatApi();
                getUserInfo();
            }
        });
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginCode();
            }
        });
    }

    private String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serial == null || serial.equals("")) {
            serial = "" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        }

        String ras_serial = VR_RsaUtils.encodeByPublicKey(serial + "_Android");
        OkhttpConstant.DEVICE_NUMBER = ras_serial;
        return serial;

    }

    private void getLoginCode() {
        progressDialog.show();
        // 加密登录手机号
        String Ras_loginPhoneNum = VR_RsaUtils.encodeByPublicKey("18319429020");
        // 加密登录密码
        String Ras_loginPassword = VR_RsaUtils.encodeByPublicKey("000000");
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("loginName", Ras_loginPhoneNum);
        hashMap.put("loginPassword", Ras_loginPassword);
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/login/service", jsonData, new GenericsCallback<LoginBean>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                Log.e("cjg", "e: " + e.getMessage());
            }

            @Override
            public void onResponse(LoginBean response, int id) {
                progressDialog.dismiss();
                String resCode = response.getResCode();
                String resDesc = response.getResDesc();
                String rsa_userId = response.getUserId();
                String userId = RasUtil.decryptByPublicKey(rsa_userId);
                String lastLoginTime = response.getLastLoginTime();
                String lastLoginIp = response.getLastLoginIp();
                int isLimitLogin = response.getIsLimitLogin();
                String rsa_imAccount = response.getImAccount();
                String imAccount = RasUtil.decryptByPublicKey(rsa_imAccount);
                String rsa_imPassword = response.getImPassword();
                String imPassword = RasUtil.decryptByPublicKey(rsa_imPassword);
                String sessionId = response.getSessionId();
                Log.e("cjg", "resCode: " + resCode + "\nresDesc: " + resDesc + "\nuserId: " + userId + "\nlastLoginTime: " + lastLoginTime +
                        "\nlastLoginIp: " + lastLoginIp + "\nisLimitLogin: " + isLimitLogin + "\nimAccount: " + imAccount + "\nimPassword: " + imPassword +
                        "\nsessionId: " + sessionId);
                if(resCode.equals("0")){
                    if(isLimitLogin == 2){
                        Log.w("cjg","当前用户属于未激活状态.");
                    }else{
                        Log.d("cjg","登录成功");
                    }
                }else if(resCode.equals("-90")){
                    Log.e("cjg","密码错误，登录失败");
                }else if(resCode.equals("-97")){
                    Log.e("cjg","账户不存在，登录失败");
                }else{
                    Log.e("cjg","resDesc: " + resDesc);
                }
            }
        });
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: getUserInfo
     * @Description: TODO 获取用户信息
     * @author hechuang
     */
    private void getUserInfo() {
        String Ras_uid = VR_RsaUtils.encodeByPublicKey("66164");
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", Ras_uid);
        hashMap.put("loginUserId", Ras_uid);
        hashMap.put("queryType", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData(
                "/user/zone/homepage/service", jsonData,
                new GenericsCallback<UserMessageResult>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        showToastMsg("网络连接失败，请检查网络！");
                    }

                    @Override
                    public void onResponse(UserMessageResult result, int id) {
                        if (result.getResCode().equals("0")) {
                           Log.e("cjg","result: " + result.toString());
                        } else {
//                            showToastMsg(result.getResDesc());
                        }
                    }
                });
    }


    private void heartBeatApi() {
        //参数mac地址
        String mac = MacUtil.getEthernetMacAddress();
        //参数sn号
        String sn = Build.SERIAL;
        //参数key
        String key = DeviceUtil.getRandomString(18);
        //参数time时间戳
        String time = new Date().getTime() + "";
        //app版本号
        String appVersion = PackageUtil.getVersionName(this);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("mac", mac);
        hashMap.put("sn", sn);
        hashMap.put("key", key);
        hashMap.put("time", time);
        hashMap.put("appVersion", appVersion);
        //转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        Log.d("cjg", "jsonData: " + jsonData);
//        OkHttpAPI.postHttpData("/v1/message/heartbest",);

    }

//    private void getValidateCode(){
//        //加密手机号
//        String Ras_registerPhoneNum = RasUtil.encodeByPublicKey("18319429020");
//        //封装参数
//        HashMap<String, Object> hashMap = new HashMap<String, Object>();
//        hashMap.put("mobile", Ras_registerPhoneNum);
//        hashMap.put("msgType", 2);
//        //转参数
//        String jsonData = StringUtil.MapToJson(hashMap);
//        OkHttpAPI.postObjectData("/send/smsCode/service", jsonData, new HttpSuccess<JSONObject>() {
//            @Override
//            public void onSuccess(JSONObject result, int id) {
//                try{
//                    String code = (String) result.get("code");
//                    if(code.equals("0")){
//                        Toast.makeText(MainActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
//                    }else if(code.equals("-91")){
//                        Toast.makeText(MainActivity.this, "手机号码已被注册", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(MainActivity.this, "您的手机号已经注册，请直接登录!", Toast.LENGTH_SHORT).show();
//                    }
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//            }
//        }, new HttpError() {
//            @Override
//            public void onError(okhttp3.Call call, Exception e, int id) {
//                Toast.makeText(MainActivity.this, "网络连接失败!请检查网络", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void goToRegister(){
//        //加密手机号
//        String Ras_registerPhoneNum = RasUtil.encodeByPublicKey("18319429020");
//        //加密登录密码
//        String Ras_registerPasswordNum = RasUtil.encodeByPublicKey("123456");
//        //加密验证码
//        String Ras_registerCode = RasUtil.encodeByPublicKey("1234");
//        //封装参数
//        HashMap<String,Object> hashMap = new HashMap<String,Object>();
//        hashMap.put("registerText",Ras_registerPhoneNum);
//        hashMap.put("loginPassword",Ras_registerPasswordNum);
//        hashMap.put("validateCode",Ras_registerCode);
//        hashMap.put("registerType",1);
//        //转参数
//        String jsonData = StringUtil.MapToJson(hashMap);
//        // {"loginPassword":"123456","validateCode":"1234","registerType":1,"registerText":"18816814524"}
//        // {"loginPassword":"dhbMD4wl+ZQfU5R2kE2/puB2xI0wI+Z3ZwXJXW/Z+ha59wR8f1WhceFeogbZjv54T9S2P8Qbmkc9B/6bK0+1wu0wEMvgCYq+1uKpb85OTlPFOljZDoU5JJGp+PplR1ZUpUJriNXu2IeXJJ5Iofpd2GiwdVjTr30A2S4vPsfAqZw\u003d",
//        // "validateCode":"wKbM7AhYFjHi5N7KihAb+r6gpqv4wYYaT+hRXaNEHmtEBrY0rbi5qh/dj75e9vf7LEHInKJ4e0ZHwpWRuy8TfznMHR/pX2XgiarztNv4gwJQg7vj3WM99C+s18Z6RaxRIpoLxmaN0uS2OLVgPE6+oHxehfaA++8Uyh4OcfgDDz8\u003d",
//        // "registerType":1,
//        // "registerText":"zxGIMXHMZW7u52TTNKJV8piv65CyZCsbBX+GLALPK1KbjUIsYGqHiGSkLeB6LDOWnbbWq8oW+7ShgEfvJql4RnovLcZd+p4RsIPCgrsQxqRsY3ybk5c9kbOmlIYiy970DAW1V2JW6l3JUW+diTzxl0UdePslhxQa/563AgQUX+A\u003d"}
//        Log.d("cjg","jsonData: " + jsonData);
//    }
}
