package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.LogUtils;

import java.io.File;

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
	private static final LogUtils.LogTag TAG = LogUtils.getLogTag(UpdateSdcardReceiver.class.getSimpleName(), false);
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG, "onReceive", "onReceive");
		if (null != intent) {
			boolean isMount = intent.getBooleanExtra(IS_MOUNT, false);//是否重新挂着了sd卡
			LogUtils.d(TAG, "onReceive", "isMount,"+isMount );
			if (isMount) {//是挂在
				//更新sd卡的路径
				String sdCardPath = intent.getStringExtra(SDCRAD_PATH);
				LogUtils.d(TAG, "onReceive", "sdCardPath,"+sdCardPath );
				FileStore.getInstance().setSdcrad(sdCardPath);
				String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
				LogUtils.d(TAG, "onReceive", "rootPath:"+rootPath );
				if (!sdCardPath.equals(rootPath)) {
					LogUtils.d(TAG, "onReceive", "copy program" );
					autoCopyProgram(rootPath);
				}
			} else {
				FileStore.getInstance().setSdcrad(null);
				ProgramPlayManager.getInstance().playProgramList("UpdateSdcard");
			}
		}
	}
	
	/**
	 * 自动拷贝机身存储的节目到外置SD卡上面
	 */
	private void autoCopyProgram(String externalDir) {
	    externalDir = externalDir + File.separator + "mallposter";
		File mallPosterFolder = new File(externalDir);
		if (mallPosterFolder.exists() && mallPosterFolder.isDirectory()) {
			FileStore.getInstance().ergodicFolderAndCopy(mallPosterFolder, "autoCopyProgram");
		}
	}
	
}
