package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.manager.ServiceManager;
import com.lzkj.aidlservice.util.ConfigSettings;

/**
 * 接受Mallposter获取终端id的广播
 *
 * @author longyihuang
 * @date 2016-3-16 下午3:19:09
 */
public class GetDeviceIdReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		ServiceManager.getInstance().sendUpdateDeviceIdReceive(ConfigSettings.getDid());
	}
}
