package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.util.FileUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月26日 下午2:48:45 
 * @version 1.0 
 * @parameter 更新sd的广播
 */
public class UpdateSdcardReceiver extends BroadcastReceiver {
	private static final String ACTION = "com.lzkj.UPDATE_SDCARD_ACTION";
	private static final String IS_MOUNT = "ismount";
	private static final String SDCRAD_PATH = "sdcradPath";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			boolean isMount = intent.getBooleanExtra(IS_MOUNT, false);//是否重新挂着了sd卡
			if (isMount) {//是挂在
				//更新sd卡的路径
				String rootPath = intent.getStringExtra(SDCRAD_PATH);
				FileUtil.getInstance().setSdcard(rootPath);
			} else {
				FileUtil.getInstance().setSdcard(null);
			}
		}
	}
	
}
