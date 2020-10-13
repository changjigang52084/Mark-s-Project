package com.xunixianshi.vrshow.actmanager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.AndroidApplication;
import com.xunixianshi.vrshow.interfaces.ReceiverNetworkInterface;
import com.xunixianshi.vrshow.receiver.ConnectionChangeReceiver;

/**
 * Created by duan on 2016/9/2.
 */
public abstract class BaseNetFra extends BaseFra {

    private ReceiverNetworkInterface mReceiverNetworkInterface;
    private Handler networkHandler;
    private boolean isRunning = false;

    @Override
    public void onPause() {
        super.onPause();
        isRunning  = false;

    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning  = true;
    }

    @Override
    public void onDestroy() {
        unregisterReceiverNetworkInterface();
        super.onDestroy();
    }

    private ConnectionChangeReceiver connectionChangeReceiver;
    /**
     * @author duanchunlin
     * @time 2016/5/14 15:18
     * 注释： 注册网络状态改变回调监听
     * 这里直接使用handler发送消息给ui 层。不需要ui使用 通知
     */
    public void registerReceiverNetworkInterface(ReceiverNetworkInterface receiverNetworkInterface) {
        if(getActivity()==null){
            return;
        }
        unregisterReceiverNetworkInterface();
        if (networkHandler == null) {
            networkHandler = new Handler();
        }
        if(connectionChangeReceiver==null){
            connectionChangeReceiver = new ConnectionChangeReceiver();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(connectionChangeReceiver, filter);
        }
        this.mReceiverNetworkInterface = receiverNetworkInterface;
        connectionChangeReceiver.registerReceiverNetworkInterface(new ReceiverNetworkInterface() {
            @Override
            public void NetworkChanged(final int state) {
                /**
                 *@author duanchunlin
                 *@time 2016/5/14 16:18
                 *注释：数据监听回调接口
                 * isWiFi true 允许数据网络，false 不允许数据网络，默认false
                 */
                if (isRunning && state == ConnectionChangeReceiver.NET_STATE_CONNECTED_MOBILE && !SimpleSharedPreferences.getBoolean("allowUseNetwork", getActivity())) {
                    if (networkHandler != null) {
                        networkHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mReceiverNetworkInterface != null) {
                                    mReceiverNetworkInterface.NetworkChanged(state);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * @author duanchunlin
     * @time 2016/5/14 15:19
     * 注释： 注销网络状态改变回调监听
     */
    public void unregisterReceiverNetworkInterface() {
        if ( connectionChangeReceiver!=null) {
            getActivity().unregisterReceiver(connectionChangeReceiver);
            if (connectionChangeReceiver.unregisterReceiverNetworkInterface()) {
                mReceiverNetworkInterface = null;
                networkHandler = null;
            }
            connectionChangeReceiver = null;
        }
    }
}
