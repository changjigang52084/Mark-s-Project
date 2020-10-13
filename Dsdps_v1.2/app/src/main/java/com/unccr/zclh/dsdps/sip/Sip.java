package com.unccr.zclh.dsdps.sip;


import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.SharedUtil;

public class Sip {

    private static final String TAG = "Sip";
    private static String name;
    private static String pw;
    private static int port;
    private static String ip;

    public static void loginSip(){
        name = SharedUtil.newInstance().getString(Constants.NAME,"");
        if(TextUtils.isEmpty(name)){
            name = Constants.defaultName;
        }
        pw = SharedUtil.newInstance().getString(Constants.PW,"");
        if(TextUtils.isEmpty(pw)){
            pw = Constants.defaultPw;
        }
        port = SharedUtil.newInstance().getInt(Constants.PORT);
        if(port == -1){
            port = Constants.defaultPort;
        }
        ip = SharedUtil.newInstance().getString(Constants.IP,"");
        if(TextUtils.isEmpty(ip)){
            ip = Constants.defaultIp;
        }
        Log.d(TAG,"loginSip" + "\nname: " + name + "\npw: " + pw + "\nport: " + port + "\nip: " + ip);
        if(SipEngine.getInstance().isonLine()){
            return;
        }
        SipEngine.getInstance().init();//先初始化
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SipEngine.getInstance().Register(
                        name,
                        pw,
                        ip,
                        port);//再注册
            }
        }, 1000);
    }

}
