package com.sunchip.adw.cloudphotoframe.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunchip.adw.cloudphotoframe.BaseActivity;
import com.sunchip.adw.cloudphotoframe.MainActivity;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.Timer.MyCountTimer;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.HttpErrorEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotoListEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.TcpClientEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.netty.NettyServerListener;
import com.sunchip.adw.cloudphotoframe.netty.NettyTcpServer;
import com.sunchip.adw.cloudphotoframe.play.PlayVideoPicActivity;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.List;

import io.netty.channel.Channel;


public class SocketService extends Service implements NettyServerListener {
    public SocketService() {

    }


    private ScoketHandler sockethandler = new ScoketHandler();

    public class ScoketHandler extends Handler {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sockethandler.postDelayed(runnable, 1000);
    }

    private void startServer() {
        NettyTcpServer nettyTcpServer = NettyTcpServer.getInstance();
        //判断是否开启的状态
        if (!nettyTcpServer.isServerStart()) {
            nettyTcpServer.setListener(SocketService.this);
            nettyTcpServer.start();
        }
    }

    //五秒判断一次service是否开启
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("Socket:", "runnable:startServer()");
            startServer();
            sockethandler.postDelayed(runnable, 5000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //退出关闭
//        NettyTcpServer.getInstance().disconnect();
    }

    private Gson gson = new Gson();

    @Override
    public void onMessageResponseServer(Object msg, String ChannelId) {
//        MyCountTimer.setStartMyCount("Socket-------onMessageResponseServer");
        Type listType = new TypeToken<TcpClientEvent>() {
        }.getType();
        String Key = "158";
        final TcpClientEvent newsInfos = gson.fromJson(msg.toString(), listType);
        if (newsInfos.getType().equals("KeyEvent")) {
            if ("休眠".equals(newsInfos.getContent())) {
                SystemInterfaceUtils.getInstance().SetAdbKeyevent("input keyevent " + 349 + "\n");
//                Key = "820";
            } else {
                SystemInterfaceUtils.getInstance().SetAdbKeyevent("input keyevent " + newsInfos.getContent() + "\n");
              /*  if (newsInfos.getContent().equals("19")) {
                    Key = "103";
                } else if (newsInfos.getContent().equals("20")) {
                    Key = "108";
                } else if (newsInfos.getContent().equals("21")) {
                    Key = "105";
                } else if (newsInfos.getContent().equals("22")) {
                    Key = "106";
                } else if (newsInfos.getContent().equals("66")) {
                    Key = "28";
                } else if (newsInfos.getContent().equals("353")) {
                    Key = "824";
                } else if (newsInfos.getContent().equals("351")) {
                    Key = "822";
                } else if (newsInfos.getContent().equals("4")) {
                    Key = "158";
                }

                *//*KEY_GTW_PIR_HOME 827
                    KEY_BACK         158
                    KEY_GTW_POWER    820

                        KEY_UP           103
                    KEY_LEFT         105
                    KEY_ENTER        28
                    KEY_RIGHT         106
                    KEY_DOWN          108

                    KEY_GTW_PICTURE  822
                    KEY_GTW_VIDEO    823
                    KEY_GTW_SETTING  824*//*
                final String finalKey = Key;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SystemInterfaceUtils.getInstance().SetAdbKeyevent("sendevent /dev/input/event0  1 " + finalKey + " 1;  echo $?" + "\n");
                        SystemInterfaceUtils.getInstance().SetAdbKeyevent("sendevent /dev/input/event0  0 0 0;  echo $?" + "\n");
                    }
                }, 0);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SystemInterfaceUtils.getInstance().SetAdbKeyevent("sendevent /dev/input/event0  1 " + finalKey + " 0;  echo $?" + "\n");
                        SystemInterfaceUtils.getInstance().SetAdbKeyevent("sendevent /dev/input/event0  0 0 0;  echo $?" + "\n");
                    }
                }, 300);*/

            }

//            sendevent /dev/input/event0  1 824 1;  echo $?
//            sendevent /dev/input/event0  0 0 0 ;   echo $?
//
//            sendevent /dev/input/event0  1 824 0;  echo $?
//            sendevent /dev/input/event0  0 0 0;    echo $?

            EventBus.getDefault().post(new BaseErrarEvent(newsInfos.getContent(), 9999));
        } else if (newsInfos.getType().equals("volume")) {
            //音量
            Log.e("TAG", "Volume:" + Integer.parseInt(newsInfos.getContent()));
            AudioManger.getInstance().SetMusicVolume(Integer.parseInt(newsInfos.getContent()), true);
            HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, CloudFrameApp.getCloudFrameApp());
        } else if (newsInfos.getType().equals("shuffle")) {
            if (SharedUtil.newInstance().getInt("shuffle") == 0) {
                //那么就关掉随机
                SharedUtil.newInstance().setInt("shuffle", 1);
            } else {
                SharedUtil.newInstance().setInt("shuffle", 0);
            }
            HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, CloudFrameApp.getCloudFrameApp());
        }
    }

    private Handler mHandler = new Handler();


    @Override
    public void onStartServer() {

    }

    @Override
    public void onStopServer() {

    }

    //连接
    @Override
    public void onChannelConnect(Channel channel) {
        CloudFrameApp.IsSocketPhone = true;
    }

    //断开连接
    @Override
    public void onChannelDisConnect(Channel channel) {
        CloudFrameApp.IsSocketPhone = false;
    }
}
