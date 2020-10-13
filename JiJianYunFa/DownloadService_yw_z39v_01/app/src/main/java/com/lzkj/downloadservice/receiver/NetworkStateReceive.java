package com.lzkj.downloadservice.receiver;

import com.lzkj.downloadservice.service.ResposeDownloadService;
import com.lzkj.downloadservice.impl.RetryReportMsgImpl;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.ReportDownloadState;
import com.lzkj.downloadservice.util.ShreadUtil;
import com.lzkj.downloadservice.util.ThreadPoolManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年3月26日 下午3:21:51 
 * @version 1.0 
 * @parameter  网络变化监听
 */
public class NetworkStateReceive extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        ConnectivityManager  connectivityManager = 
        								(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  info = connectivityManager.getActiveNetworkInfo();  
        if(info != null && info.isAvailable()) {
//			retryReport();
        	//有网了，发送下载回执，
        	String downloadReport = ShreadUtil.newInstance().getString(Constant.DOWNLOAD_REPORT_KEY);
        	if (!TextUtils.isEmpty(downloadReport)) {
        		ReportDownloadState.getInstance().requetPost(downloadReport);
        	}
        	tipNetworkState(context, Constant.NETWORK_CONNECT);
        } else {//网络断开
        	tipNetworkState(context, Constant.NETWORK_DISCONNECT);
        }
	}

	private void retryReport() {
		ThreadPoolManager.get().addRunnable(new RetryReportMsgImpl());
	}
	
	private void tipNetworkState(Context context, int state) {
		Intent intent = new Intent(context, ResposeDownloadService.class);
		intent.putExtra(Constant.DOWNLOAD_TYPE_KEY, state);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		context.startService(intent);
	}
}
