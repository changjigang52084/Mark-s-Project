package com.unccr.zclh.dsdps.download;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.unccr.zclh.dsdps.db.SQLiteManager;
import com.unccr.zclh.dsdps.download.bean.RecoveryDownloadBean;
import com.unccr.zclh.dsdps.util.Constants;

import java.util.ArrayList;
/**
 * 恢复未下载完的任务
 * @author changjigang
 *
 */
public class RecoveryDownloadTaskService extends Service {

    private static final String TAG = "RecoveryDownload";

    /**恢复下载文件的类型 key**/
    public static final String TYPE = "type";
    /**恢复下载文件名的key*/
    public static final String DOWNLOAD_LIST = "download_list";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        recovery();
        stopSelf();
    }
    /***
     * 恢复下载
     */
    private void recovery() {
        ArrayList<RecoveryDownloadBean> downloadBeans = SQLiteManager.getInstance().getRecovery();
        if (null == downloadBeans || downloadBeans.isEmpty()) {
            Log.w(TAG,"recovery downloadBeans isEmpty");
            stopSelf();
            return;
        }
        startDownloadService(downloadBeans);
//		for (RecoveryDownloadBean downloadBean : downloadBeans) {
//			startDownloadService(downloadBean);
//		}
    }
    /**
     * 启动下载
     * @param downloadBeans
     */
    private void startDownloadService(ArrayList<RecoveryDownloadBean> downloadBeans) {
        Log.d(TAG,"startDownloadService...");
        Intent intent = new Intent(getApplicationContext(), ResposeDownloadService.class);
        intent.putParcelableArrayListExtra(DOWNLOAD_LIST, downloadBeans);
//		intent.putExtra(DOWNLOAD_LIST, downloadBean.getList());
        intent.putExtra(Constants.DOWNLOAD_TYPE_KEY, Constants.RECOVERY_DOWNLOAD);
        startService(intent);
//		Intent intent = new Intent(getApplicationContext(), ResposeDownloadService.class);
//		intent.putStringArrayListExtra(DOWNLOAD_LIST, downloadBean.getList());
//		intent.putExtra(Constant.DOWNLOAD_TYPE_KEY, Constant.RECOVERY_DOWNLOAD);
//		intent.putExtra(TYPE, downloadBean.getType());
//		startService(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
}
