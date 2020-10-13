package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.manager.BaiduLocationManager;
import com.lzkj.aidlservice.util.PushUitl;

/**
 * 接受获取二维码信息的广播
 * @author longyihuang
 * @date 2016-3-16 下午3:19:09
 */
public class GetQRCodeInfoReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//发送极光推送id
		PushUitl.initJPush();
		//发送个推id
		PushUitl.initGeTuiPush();
		//发送城市
		new BaiduLocationManager().startLocation();
	}
}
