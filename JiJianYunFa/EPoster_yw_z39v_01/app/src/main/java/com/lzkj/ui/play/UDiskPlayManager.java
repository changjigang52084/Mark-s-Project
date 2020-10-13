package com.lzkj.ui.play;

import java.io.File;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;

/**
 * 播放U盘节目管理器
 * @author lyhuang
 * @date 2016-1-14 下午7:10:16
 */
public class UDiskPlayManager {
	private static final LogTag TAG = LogUtils.getLogTag(UDiskPlayManager.class.getSimpleName(), true);
	private static volatile UDiskPlayManager ins;
	private final int delayMillis = 5 * 1000;
	private Handler mHandler = EPosterApp.getApplication().mHandler;
	
	private UDiskPlayManager() {}
	
	public static UDiskPlayManager getInstance() {
		if (ins == null) {
			synchronized (UDiskPlayManager.class) {
				if (ins == null) {
					ins = new UDiskPlayManager();
				}
			}
		}
		return ins;
	}
	
	
	/**
	 * 播放U盘节目
	 * @param 
	 * @return
	 */
	public void playUDiskFile(){
		File file = new File(FileStore.getInstance().getLayoutFolderPath());
    	if(file==null || !file.exists() || !file.isDirectory()){
    		LogUtils.w(TAG, "playUDiskFile", "U disk program file format error!");
    		ConfigSettings.UDISK_MODE = false;
    		FileStore.UDISK_ROOT_FOLDER = null;
    		return;
    	}
    	File [] fileLFiles = file.listFiles();
    	if(fileLFiles == null || fileLFiles.length == 0){
    		LogUtils.w(TAG, "playUDiskFile", "No program file in U disk!");
    		return;
    	}
    	File uDiskRootFolder = new File(FileStore.UDISK_ROOT_FOLDER + File.separator + FileStore.ROOT_FOLDER);
    	LogUtils.d(TAG, "playUDiskFile", "U disk folder:!" + uDiskRootFolder.getAbsolutePath());
    	if (!uDiskRootFolder.exists()) {
    		return;
    	}
    	sendProgramErrorMsg(true, StringUtil.getString(R.string.tip_udisk_copying));
    	FileStore.getInstance().ergodicFolderAndCopy(uDiskRootFolder, "playUDiskFile");
//    	mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				ProgramPlayManager.getInstance().playProgramList();
//			}
//		}, delayMillis);
	}
	
	private void sendProgramErrorMsg(boolean isShow, String tipMsg) {
		if (StringUtil.isNullStr(tipMsg)) {
			return;
		}
		Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
		if (isShow) {
			tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY, tipMsg);
		}
		tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, isShow);
		LocalBroadcastManager.getInstance(EPosterApp
				.getApplication()
				.getApplicationContext()).sendBroadcast(tipMsgIntent);
	}
	
	/**
	 * 停播U盘节目
	 * @param 
	 * @return
	 */
	public void stopUDiskFile(){
//		Toast.makeText(EPosterApp.getApplication(), "停止播放本地节目...", Toast.LENGTH_LONG).show();
		ProgramPlayManager.getInstance().playProgramList("stopUDiskFile");
	}
	

}
