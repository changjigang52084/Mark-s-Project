package com.lzkj.aidlservice.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.lzkj.aidl.DownloadAIDL;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.bean.DownloadBo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 绑定下载aidl的类
 *
 * @author changkai
 */
public class DownloadManager {
    private static final LogTag TAG = LogUtils.getLogTag(DownloadManager.class.getSimpleName(), true);
    private static final int SLEEP_TIME = 2000;
    private static final int SLEEP_FIVE_TIME = 5000;
    //	private Handler mHandler = null;
    private Timer timer = null;
    private static volatile DownloadManager downloadManager = null;
    private DownloadAIDL downloadAIDL = null;
    /**
     * 下载列表
     */
    private List<String> downloadList = null;
    /**
     * 下载类型
     */
    private int type = -1;

    private DownloadManager() {
        bindDownloadService();
        timer = new Timer();
    }

    public static DownloadManager newInstance() {
        if (null == downloadManager) {
            synchronized (DownloadManager.class) {
                if (null == downloadManager) {
                    downloadManager = new DownloadManager();
                }
            }
        }
        return downloadManager;
    }

    /**
     * 连接download service的ServiceConnection
     */
    private ServiceConnection downloadConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtils.d(TAG, "onServiceDisconnected", "service disconnetct");
            downloadAIDL = null;
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder obj) {
            LogUtils.d(TAG, "onServiceConnected", "service connected");
            downloadAIDL = DownloadAIDL.Stub.asInterface(obj);
        }
    };

    /**
     * 绑定下载服务
     */
    private boolean bindDownloadService() {
        downloadAIDL = null;
        Intent downloadServiceIntent = new Intent();
        ComponentName componentName = new ComponentName(Constant.DOWNLOADSERVICE_PKG,
                Constant.RESPOSEDOWNLOADSERVICE_CLS);
        downloadServiceIntent.setComponent(componentName);
        boolean isBindSuccess = CommunicationApp.get().bindService(downloadServiceIntent,
                downloadConnection,
                Context.BIND_AUTO_CREATE);
        if (!isBindSuccess) {
            bindDownloadService();
            LogUtils.d(TAG, "addDownloadList", "bind fail");
        }
        return isBindSuccess;
    }

    /**
     * 添加下载列表
     *
     * @param downloadFileList
     */
    public void addDownloadList(final List<String> downloadFileList, int fileType, final String prmId) {
        if (null == downloadFileList || downloadFileList.size() < 0) {
            LogUtils.d(TAG, "addDownloadList", "downloadFileList is null");
            return;
        }
        type = fileType;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    LogUtils.d(TAG, "callServiceRunnable", " run");
                    int i = 1;
                    int retry = 0;
                    int maxRetry = 10;
                    while (null == downloadAIDL) {
                        i++;
                        // 休眠两秒
                        Thread.sleep(SLEEP_TIME);
                        if ((i % 10) == 0) {
                            retry++;
                            if (retry == maxRetry) {
                                LogUtils.d(TAG, "addDownloadList", "retry is five");
                                return;
                            }
                            bindDownloadService();
                            Thread.sleep(SLEEP_FIVE_TIME);
                        }
                    }
                    LogUtils.d(TAG, "addDownloadList", "prmId: " + prmId + " ,downloadAIDL type: " + type + " ,downloadFileList: " + downloadFileList);
                    if (null != downloadAIDL && downloadAIDL.asBinder().pingBinder()) {//如果进程存在
                        if (null == downloadAIDL) {
                            LogUtils.d(TAG, "addDownloadList", "downloadAIDL is  null...");
                        } else {
                            downloadAIDL.onDownloadList(downloadFileList, type, prmId);
                        }
                    } else {
                        LogUtils.d(TAG, "addDownloadList", "downloadAIDL is null");
                    }
                } catch (Exception e) {
                    addDownload(downloadFileList, type, prmId);
                    e.printStackTrace();
                }
            }
        }, SLEEP_TIME);
    }

    /**
     * 绑定失败 使用广播的方式去下载素材
     *
     * @param httpUrls
     * @param fileType
     * @param prmId
     */
    private void addDownload(List<String> httpUrls, int fileType, String prmId) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(httpUrls);
        ArrayList<DownloadBo> downloadBos = new ArrayList<DownloadBo>();
        DownloadBo downloadBo = new DownloadBo();
        downloadBo.setHttpUrls(arrayList);
        downloadBos.add(downloadBo);
        downloadBo.setPrmId(prmId);
        downloadBo.setType(fileType);
        Bundle extras = new Bundle();
        extras.putParcelableArrayList(Constant.DOWNLOAD_LIST, downloadBos);
        startDownloadService(extras);
    }

    /**
     * 启动下载服务
     *
     * @param extras 启动服务附加的值
     */
    private void startDownloadService(Bundle extras) {
        if (null != extras) {
            ComponentName downloadComponentName = new ComponentName(Constant.DOWNLOAD_PKG,
                    Constant.DOWNLOAD_SERVICE_CLS);
            Intent downloadIntent = new Intent();
            downloadIntent.setComponent(downloadComponentName);
            downloadIntent.putExtra(Constant.DOWNLOAD_TYPE_KEY, Constant.APPEND_DOWNLOAD);//添加下载
            downloadIntent.putExtras(extras);

            CommunicationApp.get().startService(downloadIntent);
        }
        LogUtils.d(TAG, "startDownloadService", "startDownloadService");
    }

    /**
     * 取消下载素材列表
     *
     * @param cancelDownloadList 取消下载素材列表<素材名称>
     */
    public void cancelDownloadList(ArrayList<String> cancelDownloadList) {
        LogUtils.d(TAG, "cancelDownloadList", "cancelDownloadList size:" + cancelDownloadList.size());
        Intent cancelDownloadListIntent = new Intent(Constant.CANCEL_DOWNLOAD_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constant.CANCEL_DOWNLOAD_FILE_LIST, cancelDownloadList);
        cancelDownloadListIntent.putExtras(bundle);
        CommunicationApp.get().sendBroadcast(cancelDownloadListIntent);
    }

    /**
     * 解绑下载服务
     */
    public void unBindDownloadService() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtils.d(TAG, "unBindDownloadService", "unbindService");
                if (null != downloadAIDL && downloadAIDL.asBinder().pingBinder()) {
                    CommunicationApp.get().unbindService(downloadConnection);
                } else {
                    LogUtils.d(TAG, "unBindDownloadService", "downloadAIDL is null");
                }
                timer.cancel();
            }
        }, SLEEP_TIME * 2);
    }
}
