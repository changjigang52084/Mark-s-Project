package com.lzkj.ui.receiver;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.play.UDiskPlayManager;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/**
 * 监听USB插拔广播
 * 
 * @author lyhuang
 * @date 2016-1-14 下午2:03:54
 */
public class UDiskCheckReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(UDiskCheckReceiver.class.getSimpleName(), true);
	private static final String USB = "usb";
//	private PlayActivity activity;
	private boolean udiskMode = false;

//    public IntentFilter filter = new IntentFilter();
//
//	public UDiskCheckReceiver(Context context){
//		activity = (PlayActivity)context;
//		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
//		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
//		filter.addAction(Intent.ACTION_MEDIA_EJECT);
//		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
//		filter.addDataScheme("file");
//	}

//	public Intent registerReceiver() {
//		return activity.registerReceiver(this, filter);
//	}
//
//	public void unregisterReceiver() {
//		activity.unregisterReceiver(this);
//	}
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.e(TAG, "onReceive", intent.getAction());
		if (ConfigSettings.getDid() == null) {
			return;
		}
	    if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
	    	String path = intent.getData().getPath();
	    	if (!ConfigSettings.UDISK_MODE && path.contains(USB)) {
	    		LogUtils.d(TAG, "onReceive", "Insert U disk.");
	    		FileStore.UDISK_ROOT_FOLDER  = path;
	    		if(FileStore.UDISK_ROOT_FOLDER  != null && !"".equals(FileStore.UDISK_ROOT_FOLDER )){
	    			ConfigSettings.UDISK_MODE = true;
	    			LogUtils.d(TAG, "onReceive", "U disk root path:"+FileStore.UDISK_ROOT_FOLDER);
	    			UDiskPlayManager.getInstance().playUDiskFile();
	    		}
	    	}
	    } else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED) ||
	    		intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){  
	    	String path = intent.getData().getPath();
	    	if (ConfigSettings.UDISK_MODE && path.contains(USB)) {
	    		LogUtils.d(TAG, "onReceive", "Pull out U disk.");
	    		ConfigSettings.UDISK_MODE = false;
	    		FileStore.UDISK_ROOT_FOLDER = null;
	    		UDiskPlayManager.getInstance().stopUDiskFile();
	    		sendProgramErrorMsg(false, null);
	    	}
	    }
	};
	
	private void sendProgramErrorMsg(boolean isShow, String tipMsg) {
		Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
		if (isShow) {
			tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY, tipMsg);
		}
		tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, isShow);
		LocalBroadcastManager.getInstance(EPosterApp
				.getApplication()
				.getApplicationContext()).sendBroadcast(tipMsgIntent);
	}
}
