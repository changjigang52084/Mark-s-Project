package com.xunixianshi.vrshow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xunixianshi.vrshow.interfaces.ReceiverNetworkInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
*@author duanchunlin
*@time 2016/5/14 15:16
*注释： 网络状态改变监听注册器
*/
public class ConnectionChangeReceiver extends BroadcastReceiver {
    public static final int NET_STATE_NOT = 0;
    public static final int NET_STATE_NOT_CONNECTED = 1;
    public static final int NET_STATE_CONNECTED_MOBILE = 2;
    public static final int NET_STATE__CONNECTED_WIFI  = 3;

    private WeakReference<ReceiverNetworkInterface> mReceiverNetworkInterfaces;
    private int netWorkState = -1;

    public void registerReceiverNetworkInterface(ReceiverNetworkInterface receiverNetworkInterface){
        if(this.mReceiverNetworkInterfaces!=null){
            mReceiverNetworkInterfaces.clear();
        }
        this.mReceiverNetworkInterfaces = new WeakReference(receiverNetworkInterface);
    }

    public boolean unregisterReceiverNetworkInterface(){
        mReceiverNetworkInterfaces.clear();
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null ) {
            //关闭wifi网络时会接收到两次
            if(!networkInfo.isConnected()){ //没有网络
                callBackNetChangeReceive(NET_STATE_NOT_CONNECTED);
            }
            else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){ //手机网络
                callBackNetChangeReceive(NET_STATE_CONNECTED_MOBILE);
            }
            else if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){ //wifi网络
                callBackNetChangeReceive(NET_STATE__CONNECTED_WIFI);
            }
        }else {  //异常错误
            //完全关闭网络会接收到两次信息
            callBackNetChangeReceive(NET_STATE_NOT);
        }
    }

    public void callBackNetChangeReceive(int state){
        if(mReceiverNetworkInterfaces != null && netWorkState != state){
            netWorkState = state;
            ReceiverNetworkInterface receiverNetworkInterface = this.mReceiverNetworkInterfaces.get();
            if(receiverNetworkInterface!=null){
                receiverNetworkInterface.NetworkChanged(state);
            }
        }
    }
}
