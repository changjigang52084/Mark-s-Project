package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.manager.ServiceManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.HttpConfigSetting;

/**
  * @ClassName: GetMessageServerReceiver
  * @Description: TODO 接收Mallposter发送的获取工作服务器广播，并发送更新工作服务器的广播
  * @author longyihuang
  * @date 2016年6月12日 下午10:03:41
  *
  */
public class GetMessageServerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	  ServiceManager.getInstance().sendMessageServerReceive(HttpConfigSetting.getMessageServer());
	}

}
