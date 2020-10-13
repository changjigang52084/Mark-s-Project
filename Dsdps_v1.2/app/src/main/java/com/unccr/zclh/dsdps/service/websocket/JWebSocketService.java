package com.unccr.zclh.dsdps.service.websocket;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.unccr.zclh.dsdps.models.MessageEvent;
import com.unccr.zclh.dsdps.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;

public class JWebSocketService extends Service {

    public JWebSocketClient client;
    private static final String TAG = "JWebSocketService";
    private static final long HEART_BEAT_RATE = 20 * 1000 ;
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "心跳包检测websocket连接状态");
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }
            }else{
                client = null;
                initWsClient();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initWsClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        return START_STICKY;
    }

    private void initWsClient() {
        URI uri = URI.create(Constants.ws + android.os.Build.SERIAL);

        client = new JWebSocketClient(uri){
            @Override
            public void onMessage(String message) {
                Log.e(TAG, "收到的消息：" + message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String code = jsonObject.getString("code");
                    String command = jsonObject.getString("command");
                    MessageEvent messageEvent = new MessageEvent(code,command);
                    EventBus.getDefault().post(messageEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.e(TAG, "websocket连接成功");
            }
        };
        connect();
    }

    private void connect() {
        new Thread(){
            @Override
            public void run() {
                try {
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "正在重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void sendMsg(String msg) {
        if (client != null) {
            Log.e(TAG, "发送的消息：" + msg);
            client.send(msg);
        }
    }

    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    private void closeConnect() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            client = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
