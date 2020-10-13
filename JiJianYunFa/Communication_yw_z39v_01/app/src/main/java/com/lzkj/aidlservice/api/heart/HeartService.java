package com.lzkj.aidlservice.api.heart;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Server;
import com.baize.mallapp.core.utils.MD5Utils;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.manager.ValidationDeviceManager;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author kchang changkai@lz-mr.com
 * @time:2015年6月18日 上午10:57:31
 * @Description:心跳服务,发送心跳和停止心跳
 */
public class HeartService extends Service implements IRequestCallback {

    private static final String TAG = "HeartService";

    private static final String KEY_DEVICE_ID = "deviceId=";

    /**
     * 心跳默认间隔时间60秒钟
     */
    private static final int TIMER_DELAY = 1000 * 60;
    /**
     * 快发心跳默认间隔时间3小时
     */
    private static final int KUAIFA_TIMER_DELAY = 3 * 60 * 1000 * 60;
    private static final int AUTHOR_TIME_DELAY = 2 * 60 * 1000 * 60;
    /**
     * 心跳定时器
     */
    private Timer mHeartTimer;
    /**
     * 心跳定时器任务
     **/
    private TimerTask mHeartTimerTask;
    /**
     * 快发云心跳任务
     */
    //private TimerTask mKuaifaHeartTimerTask;
    /**
     * 验证是否授权定时器任务
     */
    //private TimerTask mIsAuthorTimerTask;
    /**
     * 是否能够发送心跳
     */
    private volatile boolean mIsSendHeart = true;

    private int runCounts;
    private int maxRunTimes = 20;

    //private static String APPID = "9264";
    //private static String APPKEY = "716a8b90328a39cbf289eb7b7f844cfd";

    // 正式地址
    //public static String HEARTBEAT_URL = "https://api.kuaifa.tv/screen/heartbeat?appid=9264&timestamp=";

    private long timestamp;

    @Override
    public void onCreate() {
        super.onCreate();
//		registerUpdateDidReceive();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIsSendHeart = true;
        initHeart();
        return START_STICKY;
    }

    /**
     * 初始化心跳
     */
    private void initHeart() {
        stopHeart();
        mHeartTimer = new Timer();
        mHeartTimerTask = new HeartTimerTask();
        //mKuaifaHeartTimerTask = new KuaifaHeartTimerTask();
        //mIsAuthorTimerTask = new IsAuthorTimerTask();
        //延迟一段时间以后执行心跳
        Log.i(TAG, "syncHeartTime: " + ConfigSettings.getSycnHertTime()); // 100000ms
        long sleepTime = ConfigSettings.getSycnHertTime() == 0 ? TIMER_DELAY : ConfigSettings.getSycnHertTime();
        Log.d(TAG, "sleepTime: " + sleepTime);
        //启动连接然后 发送心跳 1分钟定时发送心跳
        mHeartTimer.schedule(mHeartTimerTask, sleepTime, TIMER_DELAY);
        // 启动连接然后 发送心跳 10分钟定时发送心跳
        //mHeartTimer.schedule(mKuaifaHeartTimerTask, sleepTime, KUAIFA_TIMER_DELAY);
        // 启动连接后  发送心跳 5分钟定时发送心跳
        //mHeartTimer.schedule(mIsAuthorTimerTask, sleepTime, AUTHOR_TIME_DELAY);
        Log.d(TAG, "did: " + ConfigSettings.getDid());

    }

    /**
     * 注册更新did的广播
     */
    private void registerUpdateDidReceive() {
        IntentFilter filter = new IntentFilter(Constant.UPDATE_DID_ACTION);
        getApplicationContext().registerReceiver(updateDeviceIdBroadcastReceiver, filter);
    }

    /**
     * 更新did广播
     */
    private BroadcastReceiver updateDeviceIdBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String serverJson = intent.getStringExtra(Constant.SERVER_KEY);
            if (!StringUtil.isNullStr(serverJson)) {
                Server server = JSON.parseObject(serverJson, Server.class);
                HttpConfigSetting.saveHeartServer(server.getH());
                HttpConfigSetting.saveMessageServer(server.getM());
                HttpConfigSetting.saveRegisterServer(server.getR());
            }
            String did = intent.getStringExtra(Constant.DEVICEID_KEY);
            SharedUtil.newInstance().setString(SharedUtil.DEVICE_ID, did);
            Log.d(TAG, "update did: " + did + " ,serverJson: " + serverJson);
        }
    };

    /**
     * 定时任务器
     */
    class HeartTimerTask extends TimerTask {
        @Override
        public void run() {
            startHeart();
        }
    }

    /**
     * 快发定时任务器
     */
//    class KuaifaHeartTimerTask extends TimerTask {
//
//        @Override
//        public void run() {
//            startKuaifaHeart(createParams());
//        }
//    }

    /**
     * 验证是否授权定时任务器
     */
//    class IsAuthorTimerTask extends TimerTask {
//
//        @Override
//        public void run() {
//            startIsAuthor();
//        }
//    }

    /**
     * 开始验证设备是否授权，如果没有就给服务器发请求
     * 修改日期 2019年1月19日
     * 修改人：cjg
     */
//    private void startIsAuthor() {
//        boolean isAuthor = ConfigSettings.isClientValid();
//        Log.d(TAG, "isAuthor: " + isAuthor);
//        if (!isAuthor) {
//            ValidationDeviceManager.get().validationDevice(ConfigSettings.MAC_ADDRESS, null);
//        }
//    }

    /**
     * 发送心跳
     */
    private void startHeart() {
        Log.d(TAG, "did: " + ConfigSettings.getDid() + " ,heartUrl: " + HttpConfigSetting.getHeartUrl() + " ,runCounts: " + runCounts + " ,mIsSendHeart: " + mIsSendHeart);
        if (!StringUtil.isNullStr(ConfigSettings.getDid()) && !StringUtil.isNullStr(HttpConfigSetting.getHeartUrl()) && mIsSendHeart) {
            mIsSendHeart = false;
            String requestParam = KEY_DEVICE_ID + ConfigSettings.getDid();
            Log.i(TAG, "requestParam: " + requestParam); // deviceId=5241
            requestHttpHeart(requestParam);
        } else {
            runCounts++;
            if (runCounts > maxRunTimes) {
                mIsSendHeart = true;
                runCounts = 0;
            }
            Log.i(TAG, "HeartService startHeart did is null or heart server is null ... did is: " + ConfigSettings.getDid() + " ,heartUrl: " + HttpConfigSetting.getHeartUrl() + " ,mIsSendHeart: " + mIsSendHeart);
        }
    }

//    private JSONObject createParams() {
//        JSONObject jsonObject = new JSONObject();
//        String udid = AppUtil.getMacAddress();
//        timestamp = Long.parseLong(String.valueOf(new Date().getTime()).substring(0, 10));
//        try {
//            jsonObject.put("udid", udid);
//            jsonObject.put("time", timestamp);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }

    // 发送心跳接口请求
//    private void startKuaifaHeart(JSONObject params) {
//        HttpClient client = new DefaultHttpClient();
//        timestamp = Long.parseLong(String.valueOf(new Date().getTime()).substring(0, 10));
//        String sign = MD5Utils.string2MD5(MD5Utils.string2MD5(APPID + APPKEY) + timestamp);
//        String heartbeatUrl = HEARTBEAT_URL + timestamp + "&sign=" + sign;
//        Log.d(TAG, "heartbeatUrl: " + heartbeatUrl);
//        HttpPost post = new HttpPost(heartbeatUrl);
//        post.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
//        String result = "";
//        try {
//            StringEntity s = new StringEntity(params.toString(), "utf-8");
//            s.setContentEncoding(new BasicHeader(org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
//            post.setEntity(s);
//            // 发送请求
//            HttpResponse httpResponse = client.execute(post);
//            // 获取响应输入流
//            InputStream inputStream = httpResponse.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            inputStream.close();
//            result = sb.toString();
//            Log.d(TAG, "statusCode: " + httpResponse.getStatusLine().getStatusCode());
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                if (result != null && !"".equals(result)) {
//                    Log.d(TAG, "result: " + result);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 发送心跳请求
     *
     * @param requestParm 要发送的参数
     */
    private void requestHttpHeart(String requestParm) {
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestUrl(HttpConfigSetting.getHeartUrl());
        httpRequestBean.setRequestCallback(HeartService.this);
        httpRequestBean.setRequestParm(requestParm);
        httpRequestBean.setRequestTag(HeartService.class.getSimpleName());
        HttpUtil.newInstance().sendGetRequest(httpRequestBean);
    }

    /**
     * 停止心跳
     */
    private void stopHeart() {
        if (null != mHeartTimer) {
            try {
                mHeartTimer.cancel();
                mHeartTimer = null;
                mHeartTimerTask = null;
//                mKuaifaHeartTimerTask = null;
//                mIsAuthorTimerTask = null;
                Log.i(TAG, "stopHeart timer.cancel().");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsSendHeart = true;
        stopHeart();
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        Log.i(TAG, "result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        mIsSendHeart = true;
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        Log.i(TAG, "errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        mIsSendHeart = true;
    }
}
