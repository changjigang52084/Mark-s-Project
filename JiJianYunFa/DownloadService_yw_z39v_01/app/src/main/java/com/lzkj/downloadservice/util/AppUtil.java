package com.lzkj.downloadservice.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.lzkj.downloadservice.app.DownloadApp;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年6月17日 上午10:58:04
 * @version 1.0
 * @parameter  app工具类
 */
public class AppUtil {

	private static final String TAG = "AppUtil";

	/**
	 * 安装apk
	 * @param appPath app路径
	 */
	public static void installApkToAppPath(String appPath) {
		if (Helper.isInstallApp(DownloadApp.getContext().getApplicationContext(), Constant.CTRL_PKG)) {
			/**静默安装APP命令*/
			int silence_install = 5;
			Intent updateIntent = new Intent();
			updateIntent.setAction("com.lz.ds.ctrl.CMD_ACTION");
			updateIntent.putExtra("cmd", silence_install);
			updateIntent.putExtra("appPath", appPath);
			updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			DownloadApp.getContext().startService(updateIntent);
		} else {
			Log.d(TAG,"start installing apk.");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + appPath), "application/vnd.android.package-archive");
			DownloadApp.getContext().getApplicationContext().startActivity(intent);
		}
	}

}
