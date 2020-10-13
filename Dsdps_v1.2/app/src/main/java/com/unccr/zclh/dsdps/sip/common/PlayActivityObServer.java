package com.unccr.zclh.dsdps.sip.common;

import android.os.Handler;
import android.os.Message;

import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.util.ConfigSettings;

public class PlayActivityObServer {

    private static final String TAG = "PlayActivityObServer";

    private static final PlayActivityObServer instance = new PlayActivityObServer();

    public static Handler handler = null;
    Message msg;

    private PlayActivityObServer(){

    }

    public static PlayActivityObServer getInstance(){
        return instance;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    public void updateLocalVideoAccount(){
        if(handler != null){
            msg = Message.obtain();
            msg.what = ConfigSettings.MESSAGE_WHAT_UPDATE_LOCAL_VIDEO_ACCOUNT;
            handler.sendMessage(msg);
        }
    }
}
