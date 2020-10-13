package com.lzkj.downloadservice.receiver;

import java.io.File;

import com.lzkj.downloadservice.db.SQLiteHelper;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.upload.UploadManager;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.ThreadPoolManager;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.ShreadUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**   
* 类名称：UnbindReceiver   
* 类描述：接收解绑广播
* 创建人：lyhuang   
* 创建时间：2015-9-24 下午3:23:56    
* @version    
*    
*/
public class UnbindReceiver extends BroadcastReceiver {
	private static final LogTag LOG_TAG = LogUtils.getLogTag(UnbindReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(LOG_TAG, "onReceive","unbind stop download");
		ShreadUtil.newInstance().clearAll();
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				//停止下载，上传，删除sqlite里面的数据
				DownloadManager.newInstance().cancelAllTask();
				UploadManager.newInstance().cancelUploadThreads();
				SQLiteManager.getInstance().deleteDownloadTableData();
				SQLiteManager.getInstance().deleteUploadRecordTable();
				SQLiteManager.getInstance().deleteTableData(SQLiteHelper.TABLE_HTTP_REQUEST);
				SQLiteManager.getInstance().deleteTableData(SQLiteHelper.TABLE_FLOW);
				delete(FileUtil.getInstance().getRoot());
			}
		});
	}
	
	/**
	 * 删除所有文件
	 * @param path
	 */
	public static void delete(String path) {
		File f1 = new File(path);
		if (f1.isDirectory()) {
			String[] fileList = f1.list();
			if (null == fileList) {
				return;
			}
			for (String s : fileList) {
				File f2 = new File(f1, s);
				if (f2.isDirectory()) {
					delete(f2.getAbsolutePath());
				} else {
					f2.delete();
				}
			}
			f1.delete();
		} else {
			f1.delete();
		}
	}
}
