package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.manager.BaiduLocationManager;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

/**
 * 接受Mallposter获取终端id的广播
 *
 * @author longyihuang
 * @date 2016-3-16 下午3:19:09
 */
public class GetDeviceLocationReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		//发送城市
		String locationCity = SharedUtil.newInstance().getString(SharedUtil.LOCATION_KEY);
		if (!StringUtil.isNullStr(locationCity)) {
			notifyUpdateLoacation(locationCity);
		} else {
			new BaiduLocationManager().startLocation();
		}
	}
	
	private void notifyUpdateLoacation(String locationCity){
		if (StringUtil.isNullStr(locationCity)) {
			return;
		}
		
		SharedUtil.newInstance().setString(SharedUtil.LOCATION_KEY, locationCity);
		Intent updateLocationIntent = new Intent(Constant.LAUNCHER_UPDATE_LOCATION_ACTION);
		updateLocationIntent.putExtra(SharedUtil.LOCATION_KEY,locationCity);
		CommunicationApp.get().sendBroadcast(updateLocationIntent);
	}
}
