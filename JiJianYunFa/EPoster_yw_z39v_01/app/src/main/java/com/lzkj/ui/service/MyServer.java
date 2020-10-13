//package com.lzkj.ui.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//import com.lzkj.ui.util.LogUtils;
//
//import java.util.ArrayList;
//
//import cn.doogi.testtanzhen2.CommonUtils;
//import cn.doogi.testtanzhen2.TanZhenCallBackUtils;
//import cn.doogi.testtanzhen2.TanZhenEngine;
//import cn.doogi.testtanzhen2.bean.MyMacBean;
//
//public class MyServer extends Service {
//
//    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(MyServer.class.getSimpleName(), true);
//    private TanZhenEngine tanZhenEngine;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        start();
//    }
//
//    private void start() {
//
//        if (tanZhenEngine != null) {
//            tanZhenEngine.stop();
//            tanZhenEngine = null;
//        }
//
//        // 本机mac获取，也可以自行定义获取
//        String mac = CommonUtils.getLocalMac(getApplicationContext()).toUpperCase();
//
//        // 84:20:96:2D:38:56
//        LogUtils.d(TAG, "MyServer", "mac : " + mac);
//
//        // 若为串口探针传入TanZhenEngine.TANZHEN_SRP,若为usb探针传入TANZHEN_USB,
//        // mac = "09c0d1fb5757484880665675165166d1"
//
//        // 当使用唯一标识而不是mac地址时，请将macType设置为1
//        int macType = 0;// 使用mac
//        // int macType = 1;// 使用唯一标识
//
//        // appId与appKey可向快发询问提供，不能为空
//        String appid = "";
//        String appkey = "";
//
//        appid = "9264"; // 7166
//        appkey = "716a8b90328a39cbf289eb7b7f844cfd"; // a043100113114977e0f176781cee14b3
//
//        // 设置本地数据版本号
//        TanZhenEngine.configInfo.setDbversion(386);
//
//        tanZhenEngine = new TanZhenEngine(getApplicationContext(), TanZhenEngine.TANZHEN_SRP, mac, macType, "/dev/ttyS3", appid, appkey);
//        tanZhenEngine.start();
//
//        // 设置本地数据库保存数据的时长
//        tanZhenEngine.configInfo.setOlddata_days("1");
//
//        // 设置数据返回时间间隔
//        tanZhenEngine.setInterval(5);
//        // 获取探针类型
//        tanZhenEngine.getDetectorType();
//        // 设置时间间隔后，设置监听tanzhen
//        TanZhenCallBackUtils.getInstance().setListener("tanzhenInterval", new TanZhenCallBackUtils.MessageCallBack() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void callBack(Object value) {
//
//                for (MyMacBean myMacBean : (ArrayList<MyMacBean>) value) {
//                    LogUtils.d(TAG, "tanzhen01", " myMacBean01 : " + myMacBean.toString());
//                }
//            }
//        });
//
//        // 未设置时间间隔，设置监听tanzhen
//        TanZhenCallBackUtils.getInstance().setListener("tanzhen", new TanZhenCallBackUtils.MessageCallBack() {
//            @Override
//            public void callBack(Object value) {
//
//                for (MyMacBean myMacBean : (ArrayList<MyMacBean>) value) {
//                    LogUtils.d(TAG, "tanzhen02", "myMacBean02 : " + myMacBean.toString());
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        tanZhenEngine.stop();
//    }
//}
