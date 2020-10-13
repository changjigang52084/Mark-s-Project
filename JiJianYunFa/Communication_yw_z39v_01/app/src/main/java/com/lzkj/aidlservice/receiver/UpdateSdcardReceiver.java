package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.service.StorageSpaceService;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.LogUtils;

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
	private static final LogUtils.LogTag TAG = LogUtils.getLogTag(UpdateSdcardReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			boolean isMount = intent.getBooleanExtra(IS_MOUNT, false);//是否重新挂着了sd卡
			if (isMount) {//是挂在
				//更新sd卡的路径
				String rootPath = intent.getStringExtra(SDCRAD_PATH);
				FileUtile.getInstance().setSDCard(rootPath);
			} else {

				FileUtile.getInstance().setSDCard(null);
			}
			LogUtils.d(TAG, "onReceive", "isMount:"+isMount+",sd:"+FileUtile.getInstance().getSDCard());
			Intent updateStorageIntent = new Intent(context, StorageSpaceService.class);
			updateStorageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(updateStorageIntent);
		}
	}
	
}
