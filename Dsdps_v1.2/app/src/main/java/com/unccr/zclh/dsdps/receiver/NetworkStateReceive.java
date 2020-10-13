package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.unccr.zclh.dsdps.download.ResposeDownloadService;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.Constants;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月18日 下午5:00:19
 * @parameter NetworkStateReceive 网络变化监听
 */
public class NetworkStateReceive extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isAvailable()){
            Log.d(TAG,"网络连接");
            tipNetworkState(context,Constants.NETWORK_CONNECT);
        }else{ // 网络断开
            Log.d(TAG,"网络断开");
            tipNetworkState(context, Constants.NETWORK_DISCONNECT);
            ConfigSettings.SIP_IS_LOGIN = false;
        }
    }

    private void tipNetworkState(Context context, int state){
        Intent intent = new Intent(context, ResposeDownloadService.class);
        intent.putExtra(Constants.DOWNLOAD_TYPE_KEY,state);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }
}
