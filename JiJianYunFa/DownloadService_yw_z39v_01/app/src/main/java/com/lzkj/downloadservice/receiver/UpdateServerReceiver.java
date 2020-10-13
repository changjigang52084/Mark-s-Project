package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.ShreadUtil;

import org.apache.commons.lang.StringUtils;

/**
 * 接收communication发过来的消息服务器
 * @Description 
 * @author longyihuang
 * @date 2016年6月7日 上午11:14:36
 */
public class UpdateServerReceiver extends BroadcastReceiver {
	private static final LogTag TAG_LOG = LogUtils.getLogTag(UpdateServerReceiver.class.getSimpleName(), true);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			String workServer = intent.getStringExtra(Constant.KEY_SERVER);
			// workServer: 112.74.12.254:8080
			LogUtils.d(TAG_LOG, "onReceive", "workServer:" + workServer);
			if (!StringUtils.isEmpty(workServer)) {
				ShreadUtil.newInstance().putString(ShreadUtil.SERVER_KEY, workServer);
			}
		}
	}
}
