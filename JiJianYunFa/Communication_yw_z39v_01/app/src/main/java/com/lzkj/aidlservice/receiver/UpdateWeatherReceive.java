package com.lzkj.aidlservice.receiver;

import org.json.JSONException;

import com.lzkj.aidlservice.api.sync.RequestSyncWeather;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年5月6日 下午9:28:54 
 * @version 1.0 
 * @parameter  更新天气
 */
public class UpdateWeatherReceive extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String cacheWeather = SharedUtil.newInstance().getString(SharedUtil.CACHE_WEATHER);
		if (!StringUtil.isNullStr(cacheWeather)) {
			try {
				RequestSyncWeather.sendNewWeather(cacheWeather);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				new RequestSyncWeather().syncWeather();	
			}
		});
	}

}
