package com.lzkj.downloadservice.service;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.lzkj.aidl.DownloadUnbindAIDL;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.upload.UploadManager;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
/**
 * 解绑服务
 * @author changkai
 *
 */
public class UnbindService extends Service {
	private static final LogTag LOG_TAG = LogUtils.getLogTag(UnbindService.class.getSimpleName(), true);

	/**解绑当前终端*/
	private DownloadUnbindAIDL.Stub unbind = new DownloadUnbindAIDL.Stub() {
		@Override
		public void unbind() throws RemoteException {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LogUtils.d(LOG_TAG, "unbind", "device unbind");
					//停止下载，上传，删除sqlite里面的数据
					DownloadManager.newInstance().cancelAllTask();
					UploadManager.newInstance().cancelUploadThreads();
					SQLiteManager.getInstance().deleteDownloadTableData();
					SQLiteManager.getInstance().deleteUploadRecordTable();
					deleteFile(new File(FileUtil.getInstance().getRoot()));
				}
			}).start();
		}
	};
	/**
	 * 删除下载的节目和文件
	 */
	private void deleteFile(File folderFile ) {
		if (folderFile.exists()) {
			if (folderFile.isFile()) {//判断是否为文件如果是则删除
				folderFile.delete();
			} else if (folderFile.isDirectory()) {
				File files[] = folderFile.listFiles();
				for (File file : files) {
					deleteFile(file);
				}
				folderFile.delete();
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return unbind;
	}
	@Override
	public void onCreate() {
		LogUtils.d(LOG_TAG, "onCreate", "device unbind");
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		LogUtils.d(LOG_TAG, "onDestroy", "device unbind");
		super.onDestroy();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}
}
